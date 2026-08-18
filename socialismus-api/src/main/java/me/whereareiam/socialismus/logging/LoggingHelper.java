package me.whereareiam.socialismus.logging;

/**
 * Interface for handling logging operations in the Socialismus plugin.
 * Provides methods for logging messages at different severity levels with
 * support for message formatting.
 * <p>
 * Each logging method accepts a message template and optional objects for
 * formatting the message using placeholder substitution.
 */
@SuppressWarnings("unused")
public interface LoggingHelper {
	/**
	 * Logs an informational message.
	 *
	 * @param message the message template to log
	 * @param objects optional objects to format into the message
	 */
	void info(String message, Object... objects);

	/**
	 * Logs a warning message.
	 *
	 * @param message the message template to log
	 * @param objects optional objects to format into the message
	 */
	void warn(String message, Object... objects);

	/**
	 * Logs a severe error message and preserves the throwable cause when the
	 * platform supports stack-trace logging.
	 *
	 * @param message the message to log
	 * @param throwable the failure associated with the message
	 */
	default void severe(String message, Throwable throwable) {
		severe(message + ": " + throwable);
	}

	/**
	 * Logs a severe error message.
	 *
	 * @param message the message template to log
	 * @param objects optional objects to format into the message
	 */
	void severe(String message, Object... objects);

	/**
	 * Logs a debug message.
	 * These messages are typically only shown when debug mode is enabled.
	 *
	 * @param message the message template to log
	 * @param objects optional objects to format into the message
	 */
	void debug(String message, Object... objects);
}
