package me.whereareiam.socialismus.integration.bstats.chart;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.integration.IntegrationManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

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
        //TODO Format data
        return String.valueOf(integrationManager.getEnabledIntegrationCount());
    }
}
