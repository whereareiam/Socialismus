package me.whereareiam.socialismus.integration.bstats.chart;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.type.PlatformType;
import org.bstats.charts.CustomChart;
import org.bstats.charts.SimplePie;

@Singleton
public class PlatformTypeChart implements Chart {
	public CustomChart getChart() {
		return new SimplePie("platformType", this::getData);
	}

	private String getData() {
		return PlatformType.getType().name();
	}
}
