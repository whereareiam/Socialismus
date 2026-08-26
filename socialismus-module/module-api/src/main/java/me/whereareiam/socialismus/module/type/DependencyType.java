package me.whereareiam.socialismus.module.type;

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
	 * A plugin supplied by the active server platform. These dependencies expose
	 * their declared API packages to the module without becoming a Socialismus
	 * core integration.
	 */
	PLATFORM_COMPONENT,

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
