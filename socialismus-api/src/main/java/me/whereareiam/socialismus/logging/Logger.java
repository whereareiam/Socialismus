package me.whereareiam.socialismus.logging;

/**
 * Static facade for the platform-provided Socialismus logger.
 */
public class Logger {
	private static LoggingHelper loggingHelper;

	/**
	 * Installs the platform-specific logging implementation.
	 *
	 * @param loggingHelper the logging implementation to use
	 */
	public static void init(LoggingHelper loggingHelper) {
		Logger.loggingHelper = loggingHelper;
	}

	/**
	 * Logs an informational message.
	 *
	 * @param message the message template
	 * @param objects values used to format the message
	 */
	public static void info(String message, Object... objects) {
		loggingHelper.info(message, objects);
	}

	/**
	 * Logs a warning message.
	 *
	 * @param message the message template
	 * @param objects values used to format the message
	 */
	public static void warn(String message, Object... objects) {
		loggingHelper.warn(message, objects);
	}

	/**
	 * Logs a severe message with a throwable and preserves its stack trace.
	 *
	 * @param message the message to log
	 * @param throwable the failure associated with the message
	 */
	public static void severe(String message, Throwable throwable) {
		loggingHelper.severe(message, throwable);
	}

	/**
	 * Logs a severe formatted message.
	 *
	 * @param message the message template
	 * @param objects values used to format the message
	 */
	public static void severe(String message, Object... objects) {
		loggingHelper.severe(message, objects);
	}

	/**
	 * Logs a debug message.
	 *
	 * @param message the message template
	 * @param objects values used to format the message
	 */
	public static void debug(String message, Object... objects) {
		loggingHelper.debug(message, objects);
	}
}
