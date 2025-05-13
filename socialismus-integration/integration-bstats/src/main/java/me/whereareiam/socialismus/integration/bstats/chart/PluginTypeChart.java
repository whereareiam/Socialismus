package me.whereareiam.socialismus.integration.bstats.chart;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.type.PluginType;
import org.bstats.charts.CustomChart;
import org.bstats.charts.SimplePie;

@Singleton
public class PluginTypeChart implements Chart {
	public CustomChart getChart() {
		return new SimplePie("pluginType", this::getData);
	}

	private String getData() {
		return PluginType.getType().name();
	}
}
