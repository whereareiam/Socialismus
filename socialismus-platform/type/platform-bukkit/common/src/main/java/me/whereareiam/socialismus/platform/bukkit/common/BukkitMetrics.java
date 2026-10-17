package me.whereareiam.socialismus.platform.bukkit.common;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.integration.bstats.chart.Chart;
import me.whereareiam.socialismus.integration.bstats.chart.PlatformTypeChart;
import me.whereareiam.socialismus.integration.bstats.chart.ServerVersionChart;
import me.whereareiam.socialismus.platform.common.PlatformMetrics;
import org.bstats.charts.CustomChart;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * Reports to bStats through the Bukkit bStats instance, adding the server's platform and version.
 */
@Singleton
public class BukkitMetrics extends PlatformMetrics {
	private final org.bstats.bukkit.Metrics metrics;

	@Inject
	public BukkitMetrics(org.bstats.bukkit.Metrics metrics, Injector injector) {
		super(injector);
		this.metrics = metrics;
	}

	@Override
	@NotNull
	protected Stream<Class<? extends Chart>> platformCharts() {
		return Stream.of(PlatformTypeChart.class, ServerVersionChart.class);
	}

	@Override
	protected void addChart(@NotNull CustomChart chart) {
		metrics.addCustomChart(chart);
	}
}
