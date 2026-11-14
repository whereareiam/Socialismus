package me.whereareiam.socialismus.module.mentionator;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.CommandService;
import me.whereareiam.socialismus.service.Scheduler;
import me.whereareiam.socialismus.service.chat.ChatConditionService;
import me.whereareiam.socialismus.service.condition.ConditionTexts;
import me.whereareiam.socialismus.service.phrase.Phrasebook;
import me.whereareiam.socialismus.store.Stores;

import java.util.Set;

/**
 * Hands Mentionator's injector the services of Socialismus it uses.
 */
@RequiredArgsConstructor
public class MentionatorInjectorConfiguration extends AbstractModule {
	private final Registry<Reloadable> reloadableRegistry;
	private final Scheduler scheduler;
	private final CommandService commandService;
	private final PlayerRegistry playerRegistry;
	private final Conditions conditions;
	private final ConditionTexts conditionTexts;
	private final Phrasebook phrases;
	private final EventManager eventManager;
	private final Stores stores;
	private final WorkerProcessor<ChatMessage> workers;
	private final ChatConditionService chatConditions;
	private final com.google.inject.Provider<Set<Integration>> integrations;

	@Override
	protected void configure() {
		bind(new TypeLiteral<Registry<Reloadable>>() {}).toInstance(reloadableRegistry);
		bind(new TypeLiteral<WorkerProcessor<ChatMessage>>() {}).toInstance(workers);
		bind(new TypeLiteral<Set<Integration>>() {}).toProvider(integrations);

		bind(Scheduler.class).toInstance(scheduler);
		bind(CommandService.class).toInstance(commandService);
		bind(PlayerRegistry.class).toInstance(playerRegistry);
		bind(Conditions.class).toInstance(conditions);
		bind(ConditionTexts.class).toInstance(conditionTexts);
		bind(Phrasebook.class).toInstance(phrases);
		bind(EventManager.class).toInstance(eventManager);
		bind(Stores.class).toInstance(stores);
		bind(ChatConditionService.class).toInstance(chatConditions);
	}
}
