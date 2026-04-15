package me.whereareiam.socialismus.common.integration;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.common.provider.IntegrationProvider;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.type.IntegrationScope;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

@Singleton
public class DefaultIntegrationManager implements Reloadable {
	private final IntegrationProvider integrationProvider;
	private final Set<Integration> integrationCandidates;

	@Inject
	public DefaultIntegrationManager(
			IntegrationProvider integrationProvider,
			@Named("integrationCandidates") Set<Integration> integrationCandidates,
			Registry<Reloadable> reloadables
	) {
		this.integrationProvider = integrationProvider;
		this.integrationCandidates = integrationCandidates;
		reloadables.register(this);

		bootstrap(IntegrationScope.STARTUP_ONLY, IntegrationScope.RELOADABLE);
	}

	@Override
	public void reload() {
		bootstrap(IntegrationScope.RELOADABLE);
	}

	private void bootstrap(IntegrationScope... scopes) {
		Set<IntegrationScope> allowed = EnumSet.copyOf(Arrays.asList(scopes));
		for (Integration integration : integrationCandidates) {
			if (integration == null) continue;
			if (!allowed.contains(integration.scope())) continue;
			integration.initialize(integrationProvider);
		}
	}
}
