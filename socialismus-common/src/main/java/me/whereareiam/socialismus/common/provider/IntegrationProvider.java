package me.whereareiam.socialismus.common.provider;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.output.integration.Integration;
import me.whereareiam.socialismus.registry.Registry;

import java.util.HashSet;
import java.util.Set;

@Singleton
public class IntegrationProvider implements Provider<Set<Integration>>, Registry<Integration> {
    private final Set<Integration> integrations = new HashSet<>();

    @Override
    public void register(Integration integration) {
        integrations.add(integration);
    }

    @Override
    public Set<Integration> get() {
        return integrations;
    }
}
