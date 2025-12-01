package me.whereareiam.socialismus.output;

/**
 * Interface for providing default configuration objects in the Socialismus plugin.
 * Implementations of this interface define default values for configuration types,
 * which are used as templates or fallbacks when user configurations are missing or incomplete.
 *
 * @param <T> the type of configuration class this default config provides
 */
public interface DefaultConfig<T> {
		/**
		 * Gets the default configuration instance.
		 *
		 * @return a new instance of the default configuration
		 */
		T getDefault();
}