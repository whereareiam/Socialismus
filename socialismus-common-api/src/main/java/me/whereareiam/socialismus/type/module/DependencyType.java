package me.whereareiam.socialismus.type.module;

/**
 * Enumeration defining the types of module dependencies in the Socialismus plugin.
 * Used to categorize different modules based on their functionality and load order
 * in the plugin's modular architecture.
 */
public enum DependencyType {
	/**
	 * Modules that provide integration with external plugins or services.
	 * These modules extend the plugin's functionality through third-party connections.
	 */
	INTEGRATION,

	/**
	 * Feature modules that add specific functionality to the plugin.
	 * These are optional components that can be installed to enhance the plugin.
	 */
	MODULE,

	/**
	 * Core modules required for the plugin's essential operations.
	 * These modules are necessary for the plugin to function properly.
	 */
	BASE
}