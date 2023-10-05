package me.whereareiam.socialismus.integration;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.whereareiam.socialismus.integration.bStats.bStats;
import me.whereareiam.socialismus.integration.placeholderAPI.PlaceholderAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IntegrationManager {
    private final List<Integration> integrations = new ArrayList<>();

    @Inject
    public IntegrationManager(Injector injector) {

        List<Class<? extends Integration>> possibleIntegrations = Arrays.asList(
                bStats.class,
                PlaceholderAPI.class
        );

        for (Class<? extends Integration> integrationClass : possibleIntegrations) {
            try {
                Integration integration = injector.getInstance(integrationClass);
                integration.initialize();

                if (integration.isEnabled()) {
                    registerIntegration(integration);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void registerIntegration(Integration integration) {
        integrations.add(integration);
    }

    public int getEnabledIntegrationCount() {
        int enabledIntegrationCount = 0;

        for (Integration integration : integrations) {
            if (integration.isEnabled()) {
                enabledIntegrationCount++;
            }
        }

        return enabledIntegrationCount;
    }

    public List<Integration> getEnabledIntegrations() {
        List<Integration> enabledIntegrations = new ArrayList<>();

        for (Integration integration : integrations) {
            if (integration.isEnabled()) {
                enabledIntegrations.add(integration);
            }
        }

        return enabledIntegrations;
    }
}
