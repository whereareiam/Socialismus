package me.whereareiam.socialismus.integration.bstats.chart;

import org.bstats.bukkit.Metrics;

public interface Chart {
    void setMetrics(Metrics metrics);

    void addChart();

    String getData();
}
