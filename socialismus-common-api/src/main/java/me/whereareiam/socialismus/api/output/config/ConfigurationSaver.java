package me.whereareiam.socialismus.api.output.config;

import java.nio.file.Path;

/**
 * Interface for saving configuration objects in the Socialismus plugin.
 * Provides functionality to serialize and save configuration objects to files
 * in the appropriate format (YAML, JSON, etc.).
 */
public interface ConfigurationSaver {
		/**
		 * Saves a configuration object to the specified file path.
		 *
		 * @param path the path where the configuration will be saved
		 * @param object the configuration object to save
		 * @param <T> the type of configuration class
		 */
		<T> void save(Path path, T object);
}