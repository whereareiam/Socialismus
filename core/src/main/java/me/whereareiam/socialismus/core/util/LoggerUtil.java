package me.whereareiam.socialismus.core.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.cache.Cacheable;
import me.whereareiam.socialismus.core.config.setting.SettingsConfig;

@Singleton
public class LoggerUtil {
	private static java.util.logging.Logger bukkitLogger;
	private final SettingsConfig settingsConfig;

	@Inject
	public LoggerUtil(final SettingsConfig settingsConfig) {
		this.settingsConfig = settingsConfig;
	}

	public void setBukkitLogger(java.util.logging.Logger logger) {
		bukkitLogger = logger;
	}

	private boolean isBukkitLoggerAvailable() {
		if (bukkitLogger == null) {
			System.out.println("Bukkit LoggerUtil is not set. Make sure to set it using setBukkitLogger.");
			return false;
		}
		return true;
	}

	@Cacheable
	private void logMessage(String level, String message) {
		if (isBukkitLoggerAvailable()) {
			switch (level) {
				case "INFO":
					bukkitLogger.info(message);
					break;
				case "DEBUG":
					bukkitLogger.info("[DEBUG] " + message);
					break;
				case "TRACE":
					bukkitLogger.info("[TRACE] " + message);
					break;
				case "WARNING":
					bukkitLogger.warning(message);
					break;
				case "SEVERE":
					bukkitLogger.severe(message);
					break;
			}
		}
	}

	public void info(String message) {
		if (settingsConfig.logLevel >= 0) {
			logMessage("INFO", message);
		}
	}

	public void debug(String message) {
		if (settingsConfig.logLevel >= 1) {
			logMessage("DEBUG", message);
		}
	}

	public void trace(String message) {
		if (settingsConfig.logLevel >= 2) {
			logMessage("TRACE", message);
		}
	}

	public void warning(String message) {
		logMessage("WARNING", message);
	}

	public void severe(String message) {
		logMessage("SEVERE", message);
	}
}