package me.whereareiam.socialismus.integration;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.integration.bstats.bStats;
import me.whereareiam.socialismus.integration.placeholderapi.PlaceholderAPI;
import me.whereareiam.socialismus.integration.protocollib.ProtocolLib;
import me.whereareiam.socialismus.util.LoggerUtil;

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

        List<Class<? extends Integration>> possibleIntegrations = Arrays.asList(
                bStats.class,
                PlaceholderAPI.class,
                ProtocolLib.class
        );

        for (Class<? extends Integration> integrationClass : possibleIntegrations) {
            try {
                Integration integration = injector.getInstance(integrationClass);
                loggerUtil.debug("Initializing integration: " + integration.getName());
                integration.initialize();

                if (integration.isEnabled()) {
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
}
