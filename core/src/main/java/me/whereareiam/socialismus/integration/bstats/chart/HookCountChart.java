package me.whereareiam.socialismus.integration.bstats.chart;

import com.google.inject.Injector;
import me.whereareiam.socialismus.integration.IntegrationManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

public class HookCountChart implements Chart {
    private final Metrics metrics;
    private final Injector injector;

    public HookCountChart(Injector injector, Metrics metrics) {
        this.injector = injector;
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
        return String.valueOf(injector.getInstance(IntegrationManager.class).getEnabledIntegrationCount());
    }
}
