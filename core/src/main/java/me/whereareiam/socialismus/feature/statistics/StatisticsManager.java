package me.whereareiam.socialismus.feature.statistics;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.feature.Feature;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Path;

@Singleton
public class StatisticsManager implements Feature {
    private final LoggerUtil loggerUtil;

    @Inject
    private ChatMessageStatistic chatMessageStatistic;

    @Inject
    public StatisticsManager(LoggerUtil loggerUtil, Plugin plugin) {
        this.loggerUtil = loggerUtil;
        Path featureFolder = plugin.getDataFolder().toPath().resolve("features");
        Path statisticsFolder = featureFolder.resolve("statistics");

        loggerUtil.trace("Initializing class: " + this);

        File dir = statisticsFolder.toFile();
        if (!dir.exists()) {
            boolean isCreated = dir.mkdir();
            loggerUtil.debug("Creating statistics folder");
            if (!isCreated) {
                loggerUtil.severe("Failed to create directory: " + statisticsFolder);
            }
        }
    }

    public void saveAllStatistics() {
        chatMessageStatistic.saveStatistics();
    }

    @Override
    public boolean requiresChatListener() {
        return false;
    }

    @Override
    public boolean requiresJoinListener() {
        return false;
    }
}
