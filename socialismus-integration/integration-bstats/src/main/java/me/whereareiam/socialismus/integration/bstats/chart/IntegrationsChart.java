package me.whereareiam.socialismus.integration.bstats.chart;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.integration.Integration;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.CustomChart;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class IntegrationsChart implements Chart {
    private final Set<Integration> integrations;

    @Inject
    public IntegrationsChart(Set<Integration> integrations) {
        this.integrations = integrations;
    }

    public CustomChart getChart() {
        return new AdvancedPie("integrations", this::getData);
    }

    private Map<String, Integer> getData() {
        return integrations.stream().collect(Collectors.toMap(Integration::getName, module -> 1));
    }
}
