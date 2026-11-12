package me.whereareiam.socialismus.integration.bstats.chart;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.chassis.Extensions;
import me.whereareiam.chassis.model.ExtensionDescriptor;
import me.whereareiam.chassis.model.InstalledExtension;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.CustomChart;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Singleton
public class ModulesChart implements Chart {
    private final Provider<Extensions> modules;

    @Inject
    public ModulesChart(Provider<Extensions> modules) {
        this.modules = modules;
    }

    public CustomChart getChart() {
        return new AdvancedPie("modules", this::getData);
    }

    private Map<String, Integer> getData() {
        return modules.get().list().stream()
                .map(InstalledExtension::getDescriptor)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(ExtensionDescriptor::getName, module -> 1, Integer::sum));
    }
}
