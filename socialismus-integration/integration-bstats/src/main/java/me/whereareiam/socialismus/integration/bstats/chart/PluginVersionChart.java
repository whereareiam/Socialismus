package me.whereareiam.socialismus.integration.bstats.chart;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.Constants;
import org.bstats.charts.CustomChart;
import org.bstats.charts.DrilldownPie;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class PluginVersionChart implements Chart {
	@Override
	public CustomChart getChart() {
		return new DrilldownPie("detailedPluginVersion", this::getData);
	}

	private Map<String, Map<String, Integer>> getData() {
		Map<String, Map<String, Integer>> map = new HashMap<>();
		String version = Constants.VERSION.toLowerCase();

		Map<String, Integer> entry = new HashMap<>();

		entry.put(version, 1);
		if (version.startsWith("dev-")) {
			map.put("dev", entry);
		} else if (version.equals("dev")) {
			map.put("local", entry);
		} else {
			map.put("release", entry);
		}

		return map;
	}
}
