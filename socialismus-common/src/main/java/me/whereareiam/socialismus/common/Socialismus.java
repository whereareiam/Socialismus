package me.whereareiam.socialismus.common;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.Logger;
import me.whereareiam.socialismus.input.event.EventListener;
import me.whereareiam.socialismus.input.event.EventManager;
import me.whereareiam.socialismus.input.event.base.SocialisticEvent;
import me.whereareiam.socialismus.input.event.plugin.PluginBootstrappedEvent;
import me.whereareiam.socialismus.input.event.plugin.PluginReadyEvent;
import me.whereareiam.socialismus.input.event.plugin.PluginShutdownEvent;
import me.whereareiam.socialismus.input.event.plugin.PluginInitializedEvent;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.output.LoggingHelper;
import me.whereareiam.socialismus.output.PlatformInteractor;
import me.whereareiam.socialismus.output.command.CommandService;
import me.whereareiam.socialismus.output.listener.ListenerRegistrar;
import me.whereareiam.socialismus.output.module.ModuleService;
import me.whereareiam.socialismus.util.EventUtil;
import me.whereareiam.socialismus.common.chat.worker.base.ChatSelector;
import me.whereareiam.socialismus.common.chat.worker.base.RecipientResolver;
import me.whereareiam.socialismus.common.chat.worker.base.RecipientSelector;
import me.whereareiam.socialismus.common.chat.worker.formatted.FormatSelector;
import me.whereareiam.socialismus.common.container.ChatContainer;
import me.whereareiam.socialismus.common.printer.WelcomeBannerPrinter;
import me.whereareiam.socialismus.common.updater.UpdateScheduler;

/**
 * Core lifecycle coordinator for the Socialismus plugin.
 * <p>
 * This class mirrors the Intercept startup structure by reacting to
 * high-level lifecycle events instead of being called directly from
 * platform entrypoints.
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
		Constants.SERVER_VERSION = injector.getInstance(PlatformInteractor.class).getServerVersion();
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


