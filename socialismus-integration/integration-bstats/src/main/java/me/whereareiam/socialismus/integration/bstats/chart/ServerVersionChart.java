package me.whereareiam.socialismus.integration.bstats.chart;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.Constants;
import org.bstats.charts.CustomChart;
import org.bstats.charts.SimplePie;

@Singleton
public class ServerVersionChart implements Chart {
	public CustomChart getChart() {
		return new SimplePie("serverVersion", this::getData);
	}

	private String getData() {
		return Constants.SERVER_VERSION.name();
	}
}
