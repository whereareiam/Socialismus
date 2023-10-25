package me.whereareiam.socialismus.integration.bstats.chart;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.integration.IntegrationManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

@Singleton
public class HookCountChart implements Chart {
    private final IntegrationManager integrationManager;
    private Metrics metrics;

    @Inject
    public HookCountChart(IntegrationManager integrationManager) {
        this.integrationManager = integrationManager;
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
        return String.valueOf(integrationManager.getEnabledIntegrationCount());
    }
}
