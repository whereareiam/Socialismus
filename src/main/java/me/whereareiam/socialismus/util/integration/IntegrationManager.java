package me.whereareiam.socialismus.util.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IntegrationManager {
    private final List<Integration> integrations = new ArrayList<>();

    public IntegrationManager() {
        List<Class<? extends Integration>> possibleIntegrations = Arrays.asList(
                PlaceholderAPI.class
        );

        for (Class<? extends Integration> integrationClass : possibleIntegrations) {
            try {
                Integration integration = integrationClass.getDeclaredConstructor().newInstance();

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
