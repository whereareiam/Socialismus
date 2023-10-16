package me.whereareiam.socialismus.integration.bstats.chart;

import me.whereareiam.socialismus.platform.PlatformType;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

public class PlatformTypeChart implements Chart {
    private final Metrics metrics;

    public PlatformTypeChart(Metrics metrics) {
        this.metrics = metrics;
    }

    @Override
    public void addChart() {
        metrics.addCustomChart(
                new SimplePie("platformType", this::getData)
        );
    }

    @Override
    public String getData() {
        return PlatformType.getCurrentPlatform().name();
    }
}
