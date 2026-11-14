package me.whereareiam.socialismus.module.mentionator;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import lombok.RequiredArgsConstructor;
import me.whereareiam.chassis.Extensions;
import me.whereareiam.chassis.type.ExtensionState;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.scheduler.PeriodicalRunnableTask;
import me.whereareiam.socialismus.module.ModuleContext;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorCommands;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorMessages;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorSettings;
import me.whereareiam.socialismus.module.mentionator.command.CommandRegistrar;
import me.whereareiam.socialismus.module.mentionator.common.CommonConfiguration;
import me.whereareiam.socialismus.module.mentionator.common.completion.ChatCompletions;
import me.whereareiam.socialismus.module.mentionator.common.condition.MentionatorConditions;
import me.whereareiam.socialismus.module.mentionator.common.condition.ReachedCondition;
import me.whereareiam.socialismus.module.mentionator.common.condition.TargetedCondition;
import me.whereareiam.socialismus.module.mentionator.common.config.provider.MentionatorCommandsProvider;
import me.whereareiam.socialismus.module.mentionator.common.config.provider.MentionatorMessagesProvider;
import me.whereareiam.socialismus.module.mentionator.common.config.provider.MentionatorPhrasesProvider;
import me.whereareiam.socialismus.module.mentionator.common.config.provider.MentionatorSettingsProvider;
import me.whereareiam.socialismus.module.mentionator.common.config.provider.NotificationsProvider;
import me.whereareiam.socialismus.module.mentionator.common.config.provider.TagsProvider;
import me.whereareiam.socialismus.module.mentionator.common.notify.TagDispatch;
import me.whereareiam.socialismus.module.mentionator.common.tagging.TagHighlighter;
import me.whereareiam.socialismus.module.mentionator.common.tagging.Tagger;
import me.whereareiam.socialismus.module.mentionator.common.value.PlaceholderValues;
import me.whereareiam.socialismus.module.mentionator.network.MentionatorNetwork;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.ResourceRegistry;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.CommandService;
import me.whereareiam.socialismus.service.Scheduler;
import me.whereareiam.socialismus.service.chat.ChatConditionService;
import me.whereareiam.socialismus.service.chat.render.ChatMessageTransformer;
import me.whereareiam.socialismus.service.condition.ConditionTexts;
import me.whereareiam.socialismus.service.phrase.PhraseSource;
import me.whereareiam.socialismus.service.phrase.Phrasebook;
import me.whereareiam.socialismus.store.Stores;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class Mentionator implements SocialisticModule {
	private static final String ID = "mentionator";
	private static final String NETWORK = "socialismus-network";
	private static final String DIALOGUE = "dialogue";
	/**
	 * How often placeholder values and chat completions are looked at again; each tag's {@code refresh} decides
	 * whether its values are read.
	 */
	private static final long REFRESH_MILLIS = 5_000;

	private final Injector parentInjector;
	private final Registry<Reloadable> reloadableRegistry;
	private Injector injector;
	private PhraseSource phraseSource;
	// Set only while SocialismusNetwork is enabled, so its classes load only then.
	private MentionatorNetwork network;

	@Override
	public void enable(@NotNull ModuleContext context) {
		injector = Guice.createInjector(
				new MentionatorInjectorConfiguration(
						reloadableRegistry,
						parentInjector.getInstance(Scheduler.class),
						parentInjector.getInstance(CommandService.class),
						parentInjector.getInstance(PlayerRegistry.class),
						parentInjector.getInstance(Conditions.class),
						parentInjector.getInstance(ConditionTexts.class),
						parentInjector.getInstance(Phrasebook.class),
						parentInjector.getInstance(EventManager.class),
						parentInjector.getInstance(Stores.class),
						parentInjector.getInstance(Key.get(new TypeLiteral<WorkerProcessor<ChatMessage>>() {})),
						parentInjector.getInstance(ChatConditionService.class),
						parentInjector.getProvider(Key.get(new TypeLiteral<Set<Integration>>() {}))
				),
				new CommonConfiguration(context.dataFolder(), isEnabled(DIALOGUE))
		);

		// Writes every document, so an administrator finds them all after the first start.
		Stream.of(MentionatorSettings.class, MentionatorMessages.class, MentionatorCommands.class).forEach(injector::getInstance);
		MentionatorPhrasesProvider phrases = injector.getInstance(MentionatorPhrasesProvider.class);
		phrases.get();
		phraseSource = injector.getInstance(Phrasebook.class).add(ID, phrases::get);

		context.conditions().register(new TargetedCondition());
		context.conditions().register(injector.getInstance(ReachedCondition.class));
		// A list that cannot be bound stops the module, naming the list.
		injector.getInstance(MentionatorConditions.class).bind();

		injector.getInstance(CommandRegistrar.class).registerCommands();
		if (isEnabled(NETWORK))
			network = MentionatorNetwork.attach(parentInjector.getInstance(ResourceRegistry.class), injector).orElse(null);

		listeners().forEach(eventManager()::register);
		transformers().register(injector.getInstance(TagHighlighter.class));
		injector.getInstance(Tagger.class).start();
		injector.getInstance(Scheduler.class).schedule(PeriodicalRunnableTask.builder()
				.module(ID)
				.delay(REFRESH_MILLIS)
				.period(REFRESH_MILLIS)
				.runnable(this::refresh)
				.build());
		injector.getInstance(PlayerRegistry.class).getPlayers().forEach(player -> injector.getInstance(PlaceholderValues.class).read(player));
	}

	@Override
	public void disable() {
		injector.getInstance(Scheduler.class).cancelByModule(ID);
		injector.getInstance(Tagger.class).stop();
		transformers().unregister(injector.getInstance(TagHighlighter.class));
		listeners().forEach(eventManager()::unregister);
		if (network != null) {
			network.detach();
			network = null;
		}
		injector.getInstance(ChatCompletions.class).clear();
		injector.getInstance(MentionatorConditions.class).close();
		if (phraseSource != null) {
			phraseSource.close();
			phraseSource = null;
		}
		Stream.of(
				MentionatorSettingsProvider.class,
				MentionatorMessagesProvider.class,
				MentionatorCommandsProvider.class,
				MentionatorPhrasesProvider.class,
				TagsProvider.class,
				NotificationsProvider.class
		).map(injector::getInstance).forEach(reloadableRegistry::unregister);
	}

	private void refresh() {
		injector.getInstance(PlaceholderValues.class).refresh();
		injector.getInstance(ChatCompletions.class).refresh();
	}

	private List<EventListener> listeners() {
		return List.of(
				injector.getInstance(MentionatorConditions.class),
				injector.getInstance(TagDispatch.class),
				injector.getInstance(PlaceholderValues.class),
				injector.getInstance(ChatCompletions.class)
		);
	}

	private Registry<ChatMessageTransformer> transformers() {
		return parentInjector.getInstance(Key.get(new TypeLiteral<Registry<ChatMessageTransformer>>() {}));
	}

	/**
	 * Returns whether a module Mentionator optionally depends on is enabled, whose API or conditions it reaches only
	 * then.
	 */
	private boolean isEnabled(String id) {
		return parentInjector.getInstance(Extensions.class).find(id)
				.filter(module -> module.getState() == ExtensionState.ENABLED)
				.isPresent();
	}

	private EventManager eventManager() {
		return injector.getInstance(EventManager.class);
	}
}
