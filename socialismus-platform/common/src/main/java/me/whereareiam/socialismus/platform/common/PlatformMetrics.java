package me.whereareiam.socialismus.platform.common;

import com.google.inject.Injector;
import me.whereareiam.socialismus.integration.bstats.Metrics;
import me.whereareiam.socialismus.integration.bstats.chart.*;
import org.bstats.charts.CustomChart;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * Reports the charts every platform shares to bStats; a platform adds the charts only it can fill and hands each
 * chart to its own bStats instance.
 */
public abstract class PlatformMetrics implements Metrics {
	private final Injector injector;

	protected PlatformMetrics(@NotNull Injector injector) {
		this.injector = injector;
	}

	@Override
	public void register() {
		Stream.concat(
				Stream.of(
						PluginTypeChart.class,
						PluginVersionChart.class,
						ChatCountChart.class,
						ModulesChart.class,
						IntegrationsChart.class,
						ConfigTypeChart.class
				),
				platformCharts()
		).map(injector::getInstance).map(Chart::getChart).forEach(this::addChart);
	}

	/**
	 * Returns the charts only this platform reports, besides the shared ones.
	 *
	 * @return the chart types, none by default
	 */
	@NotNull
	protected Stream<Class<? extends Chart>> platformCharts() {
		return Stream.empty();
	}

	/**
	 * Adds a chart to the bStats instance of the platform.
	 *
	 * @param chart the chart to add
	 */
	protected abstract void addChart(@NotNull CustomChart chart);
}
