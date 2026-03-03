package me.whereareiam.socialismus.integration.bstats;

import com.google.inject.ConfigurationException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.type.IntegrationScope;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class bStatsIntegration implements Integration {
	private final Injector injector;

	private Metrics metrics;
	private boolean initialized;

	@Override
	public String getName() {
		return "bStats";
	}

	@Override
	public boolean isAvailable() {
		try {
			metrics = injector.getInstance(Metrics.class);

			return true;
		} catch (ConfigurationException e) {
			return false;
		}
	}

	@Override
	public IntegrationScope scope() {
		return IntegrationScope.STARTUP_ONLY;
	}

	@Override
	public synchronized void initialize(Registry<Integration> registry) {
		if (initialized) return;
		if (!isAvailable()) return;
		if (metrics != null) {
			metrics.register();
			registry.register(this);
			initialized = true;
		}
	}
}
