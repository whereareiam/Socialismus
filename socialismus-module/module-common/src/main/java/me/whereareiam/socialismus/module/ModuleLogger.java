package me.whereareiam.socialismus.module;

import me.whereareiam.socialismus.logging.Logger;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Hands what Chassis logs about the modules to Socialismus's logger, so it reaches the server's log with
 * Socialismus's prefix and log level setting.
 */
final class ModuleLogger implements System.Logger {
	private final String name;

	ModuleLogger(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isLoggable(Level level) {
		return level != Level.OFF;
	}

	@Override
	public void log(Level level, @Nullable ResourceBundle bundle, String message, @Nullable Throwable thrown) {
		if (thrown != null && level.getSeverity() >= Level.ERROR.getSeverity()) {
			Logger.severe(message, thrown);
			return;
		}

		String line = thrown == null ? message : message + ": " + thrown;
		write(level, line);
	}

	@Override
	public void log(Level level, @Nullable ResourceBundle bundle, String format, Object... params) {
		write(level, params == null || params.length == 0 ? format : MessageFormat.format(format, params));
	}

	// The line goes as an argument, never as the format, so a % in it is printed as it is.
	private static void write(Level level, String line) {
		switch (level) {
			case ERROR -> Logger.severe("%s", line);
			case WARNING -> Logger.warn("%s", line);
			case INFO -> Logger.info("%s", line);
			case ALL, TRACE, DEBUG -> Logger.debug("%s", line);
			case OFF -> {
			}
		}
	}
}
