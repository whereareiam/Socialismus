package me.whereareiam.socialismus.module.dialogue;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;
import me.whereareiam.chassis.Extensions;
import me.whereareiam.chassis.type.ExtensionState;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.module.ModuleContext;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueCommands;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueMessages;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.api.model.config.MessagingFormats;
import me.whereareiam.socialismus.module.dialogue.api.model.config.ModerationFormats;
import me.whereareiam.socialismus.module.dialogue.command.CommandRegistrar;
import me.whereareiam.socialismus.module.dialogue.common.CommonConfiguration;
import me.whereareiam.socialismus.module.dialogue.common.condition.DialogueConditions;
import me.whereareiam.socialismus.module.dialogue.common.condition.IgnoresCondition;
import me.whereareiam.socialismus.module.dialogue.common.config.migration.DialogueConfigMigrations;
import me.whereareiam.socialismus.module.dialogue.common.config.provider.DialoguePhrasesProvider;
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
import me.whereareiam.socialismus.service.condition.ConditionTexts;
import me.whereareiam.socialismus.service.phrase.PhraseSource;
import me.whereareiam.socialismus.service.phrase.Phrasebook;
import me.whereareiam.strata.Strata;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class Dialogue implements SocialisticModule {
	/**
	 * The module whose API Dialogue reaches only while it is enabled, as an optional dependency.
	 */
	private static final String ID = "dialogue";
	private static final String NETWORK = "socialismus-network";

	private final Injector parentInjector;
	private final Registry<Reloadable> reloadableRegistry;
	private Injector injector;
	private PhraseSource phraseSource;
	// Set only while SocialismusNetwork is enabled, so its classes load only then.
	private DialogueNetwork network;

	@Override
	public void enable(@NotNull ModuleContext context) {
		Path dataFolder = context.dataFolder();
		new Strata(List.of(DialogueConfigMigrations.on(Config.configured(), dataFolder))).migrate();

		injector = Guice.createInjector(
				new DialogueInjectorConfiguration(
						reloadableRegistry,
						parentInjector.getInstance(Scheduler.class),
						parentInjector.getInstance(CommandService.class),
						parentInjector.getInstance(PlayerRegistry.class),
						parentInjector.getInstance(Conditions.class),
						parentInjector.getInstance(ConditionTexts.class),
						parentInjector.getInstance(Phrasebook.class),
						parentInjector.getInstance(SerializationService.class),
						parentInjector.getInstance(EventManager.class)
				),
				new CommonConfiguration(dataFolder)
		);

		// Writes every document, so an administrator finds them all after the first start.
		Stream.of(
				DialogueSettings.class,
				DialogueMessages.class,
				DialogueCommands.class,
				MessagingFormats.class,
				ModerationFormats.class
		).forEach(injector::getInstance);
		DialoguePhrasesProvider phrases = injector.getInstance(DialoguePhrasesProvider.class);
		phrases.get();
		phraseSource = injector.getInstance(Phrasebook.class).add(ID, phrases::get);
		// A list that cannot be bound stops the module, naming the list.
		injector.getInstance(DialogueConditions.class).bind();
		// dialogue:ignores, for other modules' lists, such as the players Mentionator tags.
		context.conditions().register(injector.getInstance(IgnoresCondition.class));

		injector.getInstance(CommandRegistrar.class).registerCommands();
		if (isNetworkEnabled())
			network = DialogueNetwork.attach(parentInjector.getInstance(ResourceRegistry.class), injector).orElse(null);
		listeners().forEach(eventManager()::register);
	}

	@Override
	public void disable() {
		if (network != null) {
			network.detach();
			network = null;
		}
		listeners().forEach(eventManager()::unregister);
		injector.getInstance(Scheduler.class).cancelByModule(ID);
		injector.getInstance(DialogueConditions.class).close();
		if (phraseSource != null) {
			phraseSource.close();
			phraseSource = null;
		}
	}

	private List<EventListener> listeners() {
		return List.of(
				injector.getInstance(DialogueConditions.class),
				injector.getInstance(ConversationModes.class),
				injector.getInstance(ConversationChat.class),
				injector.getInstance(IgnoredChat.class),
				injector.getInstance(Spying.class)
		);
	}

	/**
	 * Returns whether SocialismusNetwork is enabled, whose API Dialogue reaches only then.
	 */
	private boolean isNetworkEnabled() {
		return parentInjector.getInstance(Extensions.class).find(NETWORK)
				.filter(module -> module.getState() == ExtensionState.ENABLED)
				.isPresent();
	}

	private EventManager eventManager() {
		return injector.getInstance(EventManager.class);
	}
}
