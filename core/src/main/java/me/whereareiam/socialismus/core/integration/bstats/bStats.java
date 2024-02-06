package me.whereareiam.socialismus.core.integration.bstats;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.whereareiam.socialismus.core.integration.Integration;
import me.whereareiam.socialismus.core.integration.IntegrationType;
import me.whereareiam.socialismus.core.integration.bstats.chart.AnnouncementCountChart;
import me.whereareiam.socialismus.core.integration.bstats.chart.ChatCountChart;
import me.whereareiam.socialismus.core.integration.bstats.chart.HookCountChart;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class bStats implements Integration {
	private final Injector injector;
	private final Plugin plugin;
	private Metrics metrics;

	@Inject
	public bStats(Injector injector, Plugin plugin) {
		this.injector = injector;
		this.plugin = plugin;
	}

	@Override
	public void initialize() {
		metrics = new Metrics((JavaPlugin) plugin, 19855);

		registerCharts();
	}

	@Override
	public String getName() {
		return "bStats";
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public IntegrationType getType() {
		return IntegrationType.INTERNAL;
	}

	private void registerCharts() {
		ChatCountChart chatCountChart = injector.getInstance(ChatCountChart.class);
		chatCountChart.setMetrics(metrics);
		chatCountChart.addChart();

		HookCountChart hookCountChart = injector.getInstance(HookCountChart.class);
		hookCountChart.setMetrics(metrics);
		hookCountChart.addChart();

		SwapperCountChart swapperCountChart = injector.getInstance(SwapperCountChart.class);
		swapperCountChart.setMetrics(metrics);
		swapperCountChart.addChart();

		AnnouncementCountChart announcementCountChart = injector.getInstance(AnnouncementCountChart.class);
		announcementCountChart.setMetrics(metrics);
		announcementCountChart.addChart();
	}
}