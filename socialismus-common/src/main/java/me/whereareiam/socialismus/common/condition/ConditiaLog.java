package me.whereareiam.socialismus.common.condition;

import me.whereareiam.socialismus.logging.Logger;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Conditia's log in Socialismus's: places that can no longer be bound, and work again, are told on the console.
 */
final class ConditiaLog implements System.Logger {
	@Override
	public String getName() {
		return "Conditia";
	}

	@Override
	public boolean isLoggable(Level level) {
		return level.getSeverity() >= Level.DEBUG.getSeverity();
	}

	@Override
	public void log(Level level, ResourceBundle bundle, String message, Throwable thrown) {
		switch (level) {
			case ERROR -> Logger.severe(message, thrown);
			case WARNING -> Logger.warn("%s", message);
			case INFO -> Logger.info("%s", message);
			default -> Logger.debug("%s", message);
		}
	}

	@Override
	public void log(Level level, ResourceBundle bundle, String format, Object... params) {
		String message = params == null || params.length == 0 ? format : MessageFormat.format(format, params);

		switch (level) {
			case ERROR -> Logger.severe("%s", message);
			case WARNING -> Logger.warn("%s", message);
			case INFO -> Logger.info("%s", message);
			default -> Logger.debug("%s", message);
		}
	}
}
