package me.whereareiam.socialismus.common;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.common.chat.worker.base.ChatSelector;
import me.whereareiam.socialismus.common.chat.worker.base.RecipientResolver;
import me.whereareiam.socialismus.common.chat.worker.base.RecipientSelector;
import me.whereareiam.socialismus.common.chat.worker.formatted.FormatSelector;
import me.whereareiam.socialismus.common.container.ChatContainer;
import me.whereareiam.socialismus.common.printer.WelcomeBannerPrinter;
import me.whereareiam.socialismus.common.updater.UpdateScheduler;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.plugin.PluginBootstrappedEvent;
import me.whereareiam.socialismus.event.plugin.PluginInitializedEvent;
import me.whereareiam.socialismus.event.plugin.PluginReadyEvent;
import me.whereareiam.socialismus.event.plugin.PluginShutdownEvent;
import me.whereareiam.socialismus.listener.ListenerRegistrar;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.module.ModuleService;
import me.whereareiam.socialismus.service.CommandService;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.util.EventUtil;

/**
 * Core lifecycle coordinator for the Socialismus plugin.
 */
@Singleton
public final class Socialismus implements EventListener {
	private final Injector injector;

	@Inject
	public Socialismus(
			Injector injector,
			EventManager eventManager
	) {
		this.injector = injector;

		// Register this listener once the event system is available
		eventManager.register(this);
	}

	@SocialisticEvent
	public void onPluginBootstrapped(PluginBootstrappedEvent event) {
		Logger.init(injector.getInstance(LoggingHelper.class));

		// Load settings early
		injector.getInstance(Settings.class);
	}

	@SocialisticEvent
	public void onPluginReady(PluginReadyEvent event) {
		// Initialize all component before first event is triggered, leads to faster response time
		injector.getInstance(ChatContainer.class);
		injector.getInstance(RecipientResolver.class);
		injector.getInstance(ChatSelector.class);
		injector.getInstance(RecipientSelector.class);
		injector.getInstance(FormatSelector.class);
		injector.getInstance(ChatMessages.class);
		injector.getInstance(ChatSettings.class);

		injector.getInstance(CommandService.class);
		injector.getInstance(ModuleService.class).loadModules();
		injector.getInstance(ListenerRegistrar.class).registerListeners();

		injector.getInstance(SynchronizationService.class).initialize();

		injector.getInstance(WelcomeBannerPrinter.class).print();
		injector.getInstance(UpdateScheduler.class).start();

		EventUtil.callEvent(new PluginInitializedEvent(), () -> {});
	}

	@SocialisticEvent
	public void onPluginShutdown(PluginShutdownEvent event) {
		// Currently no explicit shutdown logic
	}
}


