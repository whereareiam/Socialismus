package me.whereareiam.socialismus.module.dialogue;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.module.ModuleService;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueCommands;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueMessages;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.api.model.config.MessagingFormats;
import me.whereareiam.socialismus.module.dialogue.api.model.config.ModerationFormats;
import me.whereareiam.socialismus.module.dialogue.command.CommandRegistrar;
import me.whereareiam.socialismus.module.dialogue.common.CommonConfiguration;
import me.whereareiam.socialismus.module.dialogue.common.config.migration.DialogueConfigMigrations;
import me.whereareiam.socialismus.module.dialogue.common.control.IgnoredChat;
import me.whereareiam.socialismus.module.dialogue.common.conversation.ConversationChat;
import me.whereareiam.socialismus.module.dialogue.common.conversation.ConversationModes;
import me.whereareiam.socialismus.module.dialogue.common.moderation.Spying;
import me.whereareiam.socialismus.module.dialogue.network.DialogueNetwork;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.ResourceRegistry;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.CommandService;
import me.whereareiam.socialismus.service.Scheduler;
import me.whereareiam.socialismus.service.SerializationService;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;
import me.whereareiam.socialismus.service.requirement.RequirementReasonService;
import me.whereareiam.strata.Strata;

import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class Dialogue extends SocialisticModule {
	/**
	 * The module whose API Dialogue reaches only while it is loaded, as an optional dependency.
	 */
	private static final String NETWORK = "SocialismusNetwork";

	private final Injector parentInjector;
	private final Registry<Reloadable> reloadableRegistry;
	private Injector injector;
	// Set only while SocialismusNetwork is installed, so its classes load only then.
	private DialogueNetwork network;

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
						parentInjector.getInstance(RequirementReasonService.class),
						parentInjector.getInstance(SerializationService.class),
						parentInjector.getInstance(EventManager.class)
				),
				new CommonConfiguration(workingPath)
		);

		// Writes every document, so an administrator finds them all after the first start.
		Stream.of(
				DialogueSettings.class,
				DialogueMessages.class,
				DialogueCommands.class,
				MessagingFormats.class,
				ModerationFormats.class
		).forEach(injector::getInstance);
	}

	@Override
	public void onEnable() {
		injector.getInstance(CommandRegistrar.class).registerCommands();
		if (isNetworkInstalled())
			network = DialogueNetwork.attach(parentInjector.getInstance(ResourceRegistry.class), injector).orElse(null);
		listeners().forEach(eventManager()::register);
	}

	@Override
	public void onDisable() {
		if (network != null) {
			network.detach();
			network = null;
		}
		listeners().forEach(eventManager()::unregister);
		injector.getInstance(Scheduler.class).cancelByModule("dialogue");
	}

	private List<EventListener> listeners() {
		return List.of(
				injector.getInstance(ConversationModes.class),
				injector.getInstance(ConversationChat.class),
				injector.getInstance(IgnoredChat.class),
				injector.getInstance(Spying.class)
		);
	}

	/**
	 * Returns whether SocialismusNetwork is installed, whose API Dialogue reaches only then.
	 */
	private boolean isNetworkInstalled() {
		return parentInjector.getInstance(ModuleService.class).getModule(NETWORK).isPresent();
	}

	private EventManager eventManager() {
		return injector.getInstance(EventManager.class);
	}

	@Override
	public void onUnload() {
	}
}
