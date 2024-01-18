package me.whereareiam.socialismus.core.integration.bstats.chart;

import org.bstats.bukkit.Metrics;

public interface Chart {
	void setMetrics(Metrics metrics);

	void addChart();

	String getData();
}
