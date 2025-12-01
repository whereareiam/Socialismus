package me.whereareiam.socialismus.integration.bstats.chart;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.output.config.ConfigurationTypeResolver;
import org.bstats.charts.CustomChart;
import org.bstats.charts.SimplePie;

@Singleton
public class ConfigTypeChart implements Chart {
	private final ConfigurationTypeResolver typeResolver;

	@Inject
	public ConfigTypeChart(ConfigurationTypeResolver typeResolver) {
		this.typeResolver = typeResolver;
	}

	public CustomChart getChart() {
		return new SimplePie("configType", this::getData);
	}

	private String getData() {
		return typeResolver.getConfigurationType().name();
	}
}
