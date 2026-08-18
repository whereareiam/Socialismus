package me.whereareiam.socialismus.module.redis;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.registry.base.Registry;

@RequiredArgsConstructor
public class SocialismusRedisInjectorConfiguration extends AbstractModule {
	private final Registry<Reloadable> reloadableRegistry;

	@Override
	protected void configure() {
		bind(new TypeLiteral<Registry<Reloadable>>() {}).toInstance(reloadableRegistry);
	}
}
