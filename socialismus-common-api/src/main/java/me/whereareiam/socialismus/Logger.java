package me.whereareiam.socialismus;

import me.whereareiam.socialismus.output.LoggingHelper;

public class Logger {
	private static LoggingHelper loggingHelper;

	public static void init(LoggingHelper loggingHelper) {
		Logger.loggingHelper = loggingHelper;
	}

	public static void info(String message, Object... objects) {
		loggingHelper.info(message, objects);
	}

	public static void warn(String message, Object... objects) {
		loggingHelper.warn(message, objects);
	}

	public static void severe(String message, Object... objects) {
		loggingHelper.severe(message, objects);
	}

	public static void debug(String message, Object... objects) {
		loggingHelper.debug(message, objects);
	}
}
