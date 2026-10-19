package me.whereareiam.socialismus.module.dialogue;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.CommandService;
import me.whereareiam.socialismus.service.Scheduler;
import me.whereareiam.socialismus.service.SerializationService;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;
import me.whereareiam.socialismus.service.requirement.RequirementReasonService;

@RequiredArgsConstructor
public class DialogueInjectorConfiguration extends AbstractModule {
	private final Registry<Reloadable> reloadableRegistry;
	private final Scheduler scheduler;
	private final CommandService commandService;
	private final PlayerRegistry playerRegistry;
	private final RequirementEvaluatorService requirementEvaluator;
	private final RequirementReasonService requirementReasons;
	private final SerializationService serializationService;
	private final EventManager eventManager;

	@Override
	protected void configure() {
		bind(new TypeLiteral<Registry<Reloadable>>() {}).toInstance(reloadableRegistry);

		// Bind services from parent injector
		bind(Scheduler.class).toInstance(scheduler);
		bind(CommandService.class).toInstance(commandService);
		bind(PlayerRegistry.class).toInstance(playerRegistry);
		bind(RequirementEvaluatorService.class).toInstance(requirementEvaluator);
		bind(RequirementReasonService.class).toInstance(requirementReasons);
		bind(SerializationService.class).toInstance(serializationService);
		bind(EventManager.class).toInstance(eventManager);
	}
}
