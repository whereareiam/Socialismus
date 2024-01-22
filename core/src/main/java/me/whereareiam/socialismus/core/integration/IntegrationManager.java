package me.whereareiam.socialismus.core.integration;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.integration.bstats.bStats;
import me.whereareiam.socialismus.core.integration.placeholderapi.PlaceholderAPI;
import me.whereareiam.socialismus.core.integration.protocollib.ProtocolLib;
import me.whereareiam.socialismus.core.util.LoggerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
public class IntegrationManager {
	private final LoggerUtil loggerUtil;
	private final List<Integration> integrations = new ArrayList<>();
	private int integrationCount = 0;

	@Inject
	public IntegrationManager(Injector injector, LoggerUtil loggerUtil) {
		this.loggerUtil = loggerUtil;

		loggerUtil.trace("Initializing class: " + this);

		injector.getInstance(bStats.class);
		List<Class<? extends Integration>> integrations = Arrays.asList(
				PlaceholderAPI.class,
				ProtocolLib.class
		);

		for (Class<? extends Integration> integrationClass : integrations) {
			try {
				Integration integration = injector.getInstance(integrationClass);
				integration.initialize();

				if (integration.isEnabled()) {
					loggerUtil.debug("Registering integration: " + integration.getName());
					registerIntegration(integration);
					integrationCount++;
				}
			} catch (Exception e) {
				loggerUtil.severe(e.getMessage());
			}
		}
	}

	public void registerIntegration(Integration integration) {
		loggerUtil.debug("Registered integration: " + integration.getName());
		integrations.add(integration);
	}

	public int getEnabledIntegrationCount() {
		return integrationCount;
	}

	public List<Integration> getIntegrations() {
		return integrations;
	}

	public boolean isIntegrationEnabled(String integrationName) {
		for (Integration integration : integrations) {
			if (integration.getName().equals(integrationName)) {
				return integration.isEnabled();
			}
		}
		return false;
	}
}
