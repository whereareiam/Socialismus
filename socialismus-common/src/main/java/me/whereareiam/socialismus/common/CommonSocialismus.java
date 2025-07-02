package me.whereareiam.socialismus.common;

import com.google.inject.Injector;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.Serializer;
import me.whereareiam.socialismus.api.input.event.plugin.PluginInitializedEvent;
import me.whereareiam.socialismus.api.input.serializer.ComponentService;
import me.whereareiam.socialismus.api.model.chat.ChatMessages;
import me.whereareiam.socialismus.api.model.chat.ChatSettings;
import me.whereareiam.socialismus.api.output.LoggingHelper;
import me.whereareiam.socialismus.api.output.command.CommandService;
import me.whereareiam.socialismus.api.output.listener.ListenerRegistrar;
import me.whereareiam.socialismus.api.output.module.ModuleService;
import me.whereareiam.socialismus.api.util.EventUtil;
import me.whereareiam.socialismus.common.chat.worker.FormatSelector;
import me.whereareiam.socialismus.common.chat.worker.chatmessage.ChatSelector;
import me.whereareiam.socialismus.common.chat.worker.chatmessage.RecipientResolver;
import me.whereareiam.socialismus.common.chat.worker.chatmessage.RecipientSelector;
import me.whereareiam.socialismus.common.container.ChatContainer;
import me.whereareiam.socialismus.common.printer.WelcomeBannerPrinter;
import me.whereareiam.socialismus.common.updater.UpdateScheduler;

public class CommonSocialismus {
	private Injector injector;

	public void onLoad() {
		injector = CommonInjector.getInjector();

		// Static helpers
		Logger.init(injector.getInstance(LoggingHelper.class));
		Serializer.init(injector.getInstance(ComponentService.class));
	}

	public void onEnable() {
		// Initialize all component before first event is triggered, leads to faster response time
		injector.getInstance(ChatContainer.class);
		injector.getInstance(RecipientResolver.class);
		injector.getInstance(ChatSelector.class);
		injector.getInstance(RecipientSelector.class);
		injector.getInstance(FormatSelector.class);
		injector.getInstance(ChatMessages.class);
		injector.getInstance(ChatSettings.class);

		injector.getInstance(CommandService.class).registerCommands();
		injector.getInstance(ModuleService.class).loadModules();
		injector.getInstance(ListenerRegistrar.class).registerListeners();

		injector.getInstance(SynchronizationService.class).initialize();

		injector.getInstance(WelcomeBannerPrinter.class).print();
		injector.getInstance(UpdateScheduler.class).start();

		EventUtil.callEvent(new PluginInitializedEvent(), () -> {});
	}

	public void onDisable() {

	}
}
