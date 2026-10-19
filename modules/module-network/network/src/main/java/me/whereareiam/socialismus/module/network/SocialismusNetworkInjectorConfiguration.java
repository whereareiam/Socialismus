package me.whereareiam.socialismus.module.network;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.registry.base.Registry;

/**
 * Hands the module's injector what it takes from Socialismus's.
 */
@RequiredArgsConstructor
public class SocialismusNetworkInjectorConfiguration extends AbstractModule {
	private final Registry<Reloadable> reloadableRegistry;

	@Override
	protected void configure() {
		bind(new TypeLiteral<Registry<Reloadable>>() {}).toInstance(reloadableRegistry);
	}
}
