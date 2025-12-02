package me.whereareiam.socialismus.type;

import lombok.Getter;

/**
 * Enumeration of supported configuration file types in the Socialismus plugin.
 * Defines the available configuration formats and their corresponding file extensions.
 */
@Getter
public enum ConfigurationType {
		/**
		 * YAML configuration format with .yml extension
		 */
		YAML(".yml"),

		/**
		 * JSON configuration format with .json extension
		 */
		JSON(".json");

		/**
		 * The file extension associated with this configuration type
		 */
		private final String extension;

		/**
		 * Constructs a ConfigurationType with the specified file extension
		 *
		 * @param extension the file extension for this configuration type
		 */
		ConfigurationType(String extension) {
				this.extension = extension;
		}
}