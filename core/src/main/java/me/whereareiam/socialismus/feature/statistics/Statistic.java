package me.whereareiam.socialismus.feature.statistics;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public abstract class Statistic<T> {
    protected final LoggerUtil loggerUtil;
    protected final Map<String, T> statistics = new ConcurrentHashMap<>();
    protected final File statFile;

    public Statistic(LoggerUtil loggerUtil, Plugin plugin, String fileName) {
        this.loggerUtil = loggerUtil;

        Path featureFolder = plugin.getDataFolder().toPath().resolve("features");
        this.statFile = featureFolder.resolve("statistics").resolve(fileName).toFile();

        loadStatistics();
    }

    public void loadStatistics() {
        if (statFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(statFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" ");
                    if (parts.length == 2) {
                        try {
                            statistics.put(parts[0], parseValue(parts[1]));
                        } catch (NumberFormatException e) {
                            loggerUtil.severe("Error parsing count for chat " + parts[0] + ": " + e.getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                loggerUtil.severe("Error loading statistics: " + e.getMessage());
            }
        }
    }

    public void saveStatistics() {
        if (!statFile.exists()) {
            try {
                boolean isCreated = statFile.createNewFile();
                if (!isCreated) {
                    loggerUtil.severe("File already exists or an error occurred while creating the file.");
                }
            } catch (IOException e) {
                loggerUtil.severe("Error creating statistics file: " + e.getMessage());
            }
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(statFile))) {
            for (Map.Entry<String, T> entry : statistics.entrySet()) {
                out.println(entry.getKey() + " " + formatValue(entry.getValue()));
            }
        } catch (IOException e) {
            loggerUtil.severe("Error saving statistics: " + e.getMessage());
        }
    }

    protected abstract T parseValue(String value);

    protected abstract String formatValue(T value);
}
