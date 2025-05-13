package me.whereareiam.socialismus.common;

import com.google.inject.Injector;
import me.whereareiam.socialismus.api.AnsiColor;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.Serializer;
import me.whereareiam.socialismus.api.input.event.plugin.PluginInitializedEvent;
import me.whereareiam.socialismus.api.input.serializer.SerializationService;
import me.whereareiam.socialismus.api.model.chat.ChatMessages;
import me.whereareiam.socialismus.api.model.chat.ChatSettings;
import me.whereareiam.socialismus.api.output.LoggingHelper;
import me.whereareiam.socialismus.api.output.command.CommandService;
import me.whereareiam.socialismus.api.output.listener.ListenerRegistrar;
import me.whereareiam.socialismus.api.output.module.ModuleService;
import me.whereareiam.socialismus.api.type.PlatformType;
import me.whereareiam.socialismus.api.type.PluginType;
import me.whereareiam.socialismus.api.util.EventUtil;
import me.whereareiam.socialismus.common.chat.worker.FormatSelector;
import me.whereareiam.socialismus.common.chat.worker.chatmessage.ChatSelector;
import me.whereareiam.socialismus.common.chat.worker.chatmessage.RecipientResolver;
import me.whereareiam.socialismus.common.chat.worker.chatmessage.RecipientSelector;
import me.whereareiam.socialismus.common.container.ChatContainer;
import me.whereareiam.socialismus.common.provider.IntegrationProvider;
import me.whereareiam.socialismus.shared.Constants;

import java.util.ArrayList;
import java.util.List;

public class CommonSocialismus {
	private Injector injector;

	public void onEnable() {
		injector = CommonInjector.getInjector();

		// Static helpers
		Logger.init(injector.getInstance(LoggingHelper.class));
		Serializer.init(injector.getInstance(SerializationService.class));

		// Initialize all component before first event is triggered, leads to faster response time
		injector.getInstance(ChatContainer.class);
		injector.getInstance(RecipientResolver.class);
		injector.getInstance(ChatSelector.class);
		injector.getInstance(RecipientSelector.class);
		injector.getInstance(FormatSelector.class);
		injector.getInstance(ChatMessages.class);
		injector.getInstance(ChatSettings.class);

		injector.getInstance(CommandService.class).registerCommands();
		injector.getInstance(ListenerRegistrar.class).registerListeners();

		printWelcomeMessage();

		injector.getInstance(Updater.class).start();
		injector.getInstance(ModuleService.class).loadModules();

		EventUtil.callEvent(new PluginInitializedEvent(), () -> {});
	}

	public void onDisable() {

	}

	private void printWelcomeMessage() {
		LoggingHelper loggingHelper = injector.getInstance(LoggingHelper.class);
		List<String> content = new ArrayList<>();

		content.add(" ");
		content.add(AnsiColor.CYAN + "  █▀ █▀▀   " + AnsiColor.RESET + "Socialismus v" + AnsiColor.GRAY + Constants.VERSION + AnsiColor.RESET);
		content.add(AnsiColor.CYAN + "  ▄█ █▄▄   " + AnsiColor.RESET + "Platform: "
				+ AnsiColor.GRAY
				+ PlatformType.getType().toString()
				+ " [" + PluginType.getType().toString() + "]"
				+ AnsiColor.RESET
		);
		content.add(" ");
		int commandCount = injector.getInstance(CommandService.class).getCommandCount();
		content.add("  Loaded " + AnsiColor.CYAN + commandCount + AnsiColor.RESET + " command" + (commandCount == 1 ? "" : "s"));
		int chatCount = injector.getInstance(ChatContainer.class).getChats().size();
		content.add("  Loaded " + AnsiColor.CYAN + chatCount + AnsiColor.RESET + " chat" + (chatCount == 1 ? "" : "s"));
		content.add(" ");
		content.add("  Integrations:");
		injector.getInstance(IntegrationProvider.class).get().forEach(integration -> content.add("    - " + AnsiColor.GREEN + integration.getName() + AnsiColor.RESET));
		content.add(" ");

		content.forEach(loggingHelper::info);
	}
}
