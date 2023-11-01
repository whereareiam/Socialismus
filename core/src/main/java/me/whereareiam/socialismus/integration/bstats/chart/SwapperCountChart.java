package me.whereareiam.socialismus.integration.bstats.chart;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.module.ModuleLoader;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

@Singleton
public class SwapperCountChart implements Chart {
    private final ModuleLoader moduleLoader;
    private Metrics metrics;

    @Inject
    public SwapperCountChart(ModuleLoader moduleLoader) {
        this.moduleLoader = moduleLoader;
    }

    @Override
    public void setMetrics(Metrics metrics) {
        this.metrics = metrics;
    }

    @Override
    public void addChart() {
        metrics.addCustomChart(
                new SimplePie("swapperCount", this::getData)
        );
    }

    @Override
    public String getData() {
        int swapperCount = moduleLoader.getSwapperCount();
        return switch (swapperCount) {
            case 0 -> "NONE";
            case 1, 2, 3, 4 -> String.valueOf(swapperCount);
            case 5, 6, 7, 8, 9, 10 -> "5-10";
            case 11, 12, 13, 14, 15, 16, 17, 18, 19 -> "10-20";
            case 20, 21, 22, 23, 24, 25, 26, 27, 28,
                    29, 30, 31, 32, 33, 34, 35, 36,
                    37, 38, 39, 40 -> "20-40";
            default -> "40+";
        };
    }
}
