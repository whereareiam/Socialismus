package me.whereareiam.socialismus.util;

import com.google.inject.Inject;
import me.whereareiam.socialismus.config.setting.SettingsConfig;

public class LoggerUtil {
    private static java.util.logging.Logger bukkitLogger;
    public void setBukkitLogger(java.util.logging.Logger logger) {
        bukkitLogger = logger;
    }

    private final SettingsConfig settingsConfig;

    @Inject
    public LoggerUtil(final SettingsConfig settingsConfig) {
        this.settingsConfig = settingsConfig;
    }

    private boolean isBukkitLoggerAvailable() {
        if (bukkitLogger == null) {
            System.out.println("Bukkit LoggerUtil is not set. Make sure to set it using setBukkitLogger.");
            return false;
        }
        return true;
    }

    public void info(String message) {
        if (isBukkitLoggerAvailable()) {
            bukkitLogger.info(message);
        }
    }

    public void debug(String message) {
        System.out.println(settingsConfig.debug);
        if (!settingsConfig.debug) {
            return;
        }

        if (isBukkitLoggerAvailable()) {
            bukkitLogger.info("[DEBUG] " + message);
        }
    }

    public void warning(String message) {
        if (isBukkitLoggerAvailable()) {
            bukkitLogger.warning(message);
        }
    }

    public void severe(String message) {
        if (isBukkitLoggerAvailable()) {
            bukkitLogger.severe(message);
        }
    }
}
