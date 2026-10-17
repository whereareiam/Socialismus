package me.whereareiam.socialismus.module.essentials;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.CommandService;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.service.SerializationService;
import me.whereareiam.socialismus.service.resource.CacheService;
import me.whereareiam.socialismus.service.resource.sync.SyncService;

@RequiredArgsConstructor
public class EssentialsInjectorConfiguration extends AbstractModule {
	private final Registry<Reloadable> reloadableRegistry;
	private final CommandService commandService;
	private final PlayerRegistry playerRegistry;
	private final PlatformInteractor platformInteractor;
	private final SerializationService serializationService;
	private final CacheService cacheService;
	private final SyncService syncService;

	@Override
	protected void configure() {
		bind(new TypeLiteral<Registry<Reloadable>>() {}).toInstance(reloadableRegistry);

		// Bind services from parent injector
		bind(CommandService.class).toInstance(commandService);
		bind(PlayerRegistry.class).toInstance(playerRegistry);
		bind(PlatformInteractor.class).toInstance(platformInteractor);
		bind(SerializationService.class).toInstance(serializationService);
		bind(CacheService.class).toInstance(cacheService);
		bind(SyncService.class).toInstance(syncService);
	}
}
