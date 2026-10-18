package me.whereareiam.socialismus.module.dialogue;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueCommands;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueMessages;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSync;
import me.whereareiam.socialismus.module.dialogue.api.model.config.MessagingFormats;
import me.whereareiam.socialismus.module.dialogue.command.CommandRegistrar;
import me.whereareiam.socialismus.module.dialogue.common.CommonConfiguration;
import me.whereareiam.socialismus.module.dialogue.common.config.migration.DialogueConfigMigrations;
import me.whereareiam.socialismus.module.dialogue.common.control.IgnoredChat;
import me.whereareiam.socialismus.module.dialogue.common.conversation.ConversationChat;
import me.whereareiam.socialismus.module.dialogue.common.conversation.ConversationModes;
import me.whereareiam.socialismus.module.dialogue.common.delivery.DialogueNetworkBridge;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.CommandService;
import me.whereareiam.socialismus.service.Scheduler;
import me.whereareiam.socialismus.service.SerializationService;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;
import me.whereareiam.socialismus.service.resource.sync.SyncService;
import me.whereareiam.strata.Strata;

import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class Dialogue extends SocialisticModule {
	private final Injector parentInjector;
	private final Registry<Reloadable> reloadableRegistry;
	private Injector injector;

	@Override
	public void onLoad() {
		new Strata(List.of(DialogueConfigMigrations.on(Config.configured(), workingPath))).migrate();

		injector = Guice.createInjector(
				new DialogueInjectorConfiguration(
						reloadableRegistry,
						parentInjector.getInstance(Scheduler.class),
						parentInjector.getInstance(CommandService.class),
						parentInjector.getInstance(PlayerRegistry.class),
						parentInjector.getInstance(RequirementEvaluatorService.class),
						parentInjector.getInstance(SerializationService.class),
						parentInjector.getInstance(SyncService.class),
						parentInjector.getInstance(EventManager.class)
				),
				new CommonConfiguration(workingPath)
		);

		// Writes every document, so an administrator finds them all after the first start.
		Stream.of(
				DialogueSettings.class,
				DialogueMessages.class,
				DialogueCommands.class,
				DialogueSync.class,
				MessagingFormats.class
		).forEach(injector::getInstance);
	}

	@Override
	public void onEnable() {
		injector.getInstance(CommandRegistrar.class).registerCommands();
		injector.getInstance(DialogueNetworkBridge.class).initialize();
		listeners().forEach(eventManager()::register);
	}

	@Override
	public void onDisable() {
		listeners().forEach(eventManager()::unregister);
		injector.getInstance(Scheduler.class).cancelByModule("dialogue");
	}

	private List<EventListener> listeners() {
		return List.of(
				injector.getInstance(ConversationModes.class),
				injector.getInstance(ConversationChat.class),
				injector.getInstance(IgnoredChat.class)
		);
	}

	private EventManager eventManager() {
		return injector.getInstance(EventManager.class);
	}

	@Override
	public void onUnload() {
	}
}
