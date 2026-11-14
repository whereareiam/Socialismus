package me.whereareiam.socialismus.module.dialogue;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.CommandService;
import me.whereareiam.socialismus.service.Phrasebook;
import me.whereareiam.socialismus.service.Scheduler;
import me.whereareiam.socialismus.service.SerializationService;
import me.whereareiam.socialismus.service.condition.ConditionTexts;
import me.whereareiam.socialismus.service.style.Styles;

@RequiredArgsConstructor
public class DialogueInjectorConfiguration extends AbstractModule {
	private final Registry<Reloadable> reloadableRegistry;
	private final Scheduler scheduler;
	private final CommandService commandService;
	private final PlayerRegistry playerRegistry;
	private final Conditions conditions;
	private final ConditionTexts conditionTexts;
	private final Phrasebook phrases;
	private final SerializationService serializationService;
	private final EventManager eventManager;
	private final Styles styles;

	@Override
	protected void configure() {
		bind(new TypeLiteral<Registry<Reloadable>>() {}).toInstance(reloadableRegistry);

		// Bind services from parent injector
		bind(Scheduler.class).toInstance(scheduler);
		bind(CommandService.class).toInstance(commandService);
		bind(PlayerRegistry.class).toInstance(playerRegistry);
		bind(Conditions.class).toInstance(conditions);
		bind(ConditionTexts.class).toInstance(conditionTexts);
		bind(Phrasebook.class).toInstance(phrases);
		bind(SerializationService.class).toInstance(serializationService);
		bind(EventManager.class).toInstance(eventManager);
		bind(Styles.class).toInstance(styles);
	}
}
