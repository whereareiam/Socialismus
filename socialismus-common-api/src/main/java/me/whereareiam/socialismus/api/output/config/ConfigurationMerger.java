package me.whereareiam.socialismus.api.output.config;

/**
 * Interface for merging configuration objects in the Socialismus plugin.
 * Provides functionality to merge user configurations with default configurations,
 * ensuring that missing values are populated with defaults while preserving
 * user-defined settings.
 */
public interface ConfigurationMerger {
		/**
		 * Merges a configuration object with its default configuration.
		 *
		 * @param config the user configuration to be updated
		 * @param defaultConfig the default configuration to merge from
		 * @param <T> the type of configuration class
		 */
		<T> void merge(T config, T defaultConfig);
}