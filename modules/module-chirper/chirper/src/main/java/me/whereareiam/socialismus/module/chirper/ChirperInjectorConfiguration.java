package me.whereareiam.socialismus.module.chirper;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.CommandService;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.service.Scheduler;

@RequiredArgsConstructor
public class ChirperInjectorConfiguration extends AbstractModule {
	private final Scheduler scheduler;
	private final PlatformInteractor platformInteractor;

	private final Registry<Reloadable> reloadableRegistry;
	private final EventManager eventManager;
	private final Conditions conditions;

	private final CommandService commandService;
	private final PlayerRegistry playerRegistry;

	@Override
	protected void configure() {
		bind(Scheduler.class).toInstance(scheduler);
		bind(PlatformInteractor.class).toInstance(platformInteractor);
		bind(Conditions.class).toInstance(conditions);

		bind(new TypeLiteral<Registry<Reloadable>>() {}).toInstance(reloadableRegistry);
		bind(EventManager.class).toInstance(eventManager);

		bind(CommandService.class).toInstance(commandService);
		bind(PlayerRegistry.class).toInstance(playerRegistry);
	}
}
