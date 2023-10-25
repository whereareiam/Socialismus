package me.whereareiam.socialismus.integration.bstats.chart;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.integration.IntegrationManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

import java.util.stream.Collectors;

@Singleton
public class HookCountChart implements Chart {
    private final Injector injector;
    private Metrics metrics;

    @Inject
    public HookCountChart(Injector injector) {
        this.injector = injector;
    }

    @Override
    public void setMetrics(Metrics metrics) {
        this.metrics = metrics;
    }

    @Override
    public void addChart() {
        metrics.addCustomChart(
                new SimplePie("hookCount", this::getData)
        );
    }

    @Override
    public String getData() {
        final IntegrationManager integrationManager = injector.getInstance(IntegrationManager.class);
        return integrationManager.getIntegrations().stream()
                .filter(Integration::isEnabled)
                .map(integration -> integration.getName().toUpperCase())
                .collect(Collectors.joining(", "));
    }
}
