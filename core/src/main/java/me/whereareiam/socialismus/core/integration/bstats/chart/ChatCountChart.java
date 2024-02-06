package me.whereareiam.socialismus.core.integration.bstats.chart;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.module.ChatController;
import me.whereareiam.socialismus.core.SocialismusModuleLoader;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

@Singleton
public class ChatCountChart implements Chart {
	private final SocialismusModuleLoader moduleLoader;
	private Metrics metrics;

	@Inject
	public ChatCountChart(SocialismusModuleLoader moduleLoader) {
		this.moduleLoader = moduleLoader;
	}

	@Override
	public void setMetrics(Metrics metrics) {
		this.metrics = metrics;
	}

	@Override
	public void addChart() {
		metrics.addCustomChart(
				new SimplePie("chatCount", this::getData)
		);
	}

	@Override
	public String getData() {
		int chatCount = ((ChatController) moduleLoader.getModule("chat")).getChats().size();
		return switch (chatCount) {
			case 0 -> "NONE";
			case 1, 2, 3, 4 -> String.valueOf(chatCount);
			case 5, 6, 7, 8, 9, 10 -> "5-10";
			case 11, 12, 13, 14, 15 -> "10-15";
			case 16, 17, 18, 19, 20 -> "15-20";
			default -> "20+";
		};
	}
}
