package me.whereareiam.socialismus.platform.common;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.integration.bstats.bStatsIntegration;
import me.whereareiam.socialismus.integration.packetevents.PacketEventsIntegration;

/**
 * Bindings every platform shares: the integrations Socialismus offers wherever it runs. A platform adds the
 * integrations only it supports to the same {@code integrationCandidates} set.
 */
public class PlatformConfiguration extends AbstractModule {
	@Override
	protected void configure() {
		Multibinder<Integration> integrations = Multibinder.newSetBinder(binder(), Integration.class, Names.named("integrationCandidates"));
		integrations.addBinding().to(PacketEventsIntegration.class);
		integrations.addBinding().to(bStatsIntegration.class);
	}
}
