package me.whereareiam.socialismus.integration.bstats.chart;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.module.ModuleService;
import me.whereareiam.socialismus.module.model.Module;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.CustomChart;

import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class ModulesChart implements Chart {
    private final ModuleService moduleService;

    @Inject
    public ModulesChart(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    public CustomChart getChart() {
        return new AdvancedPie("modules", this::getData);
    }

    private Map<String, Integer> getData() {
        return moduleService.getModules().stream().collect(Collectors.toMap(Module::getName, module -> 1));
    }
}
