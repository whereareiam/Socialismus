package me.whereareiam.socialismus.api.exception;

/**
 * Exception thrown when configuration loading fails in the Socialismus plugin.
 * This runtime exception indicates errors during configuration file parsing,
 * validation, or loading.
 */
public class ConfigLoadException extends RuntimeException {
		/**
		 * Constructs a new ConfigLoadException with the specified detail message and cause.
		 *
		 * @param message the detail message describing the error
		 * @param cause the cause of the configuration loading failure
		 */
		public ConfigLoadException(String message, Throwable cause) {
				super(message, cause);
		}
}