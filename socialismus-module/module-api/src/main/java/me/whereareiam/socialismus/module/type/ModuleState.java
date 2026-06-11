package me.whereareiam.socialismus.module.type;

/**
 * Represents the possible states of a module in the Socialismus plugin system.
 * This enum tracks the lifecycle states of modules from loading to unloading.
 */
public enum ModuleState {
	/**
	 * Module is successfully loaded into memory but not yet enabled
	 */
	LOADED,

	/**
	 * Module is loaded and running normally
	 */
	ENABLED,

	/**
	 * Module is loaded but currently disabled
	 */
	DISABLED,

	/**
	 * Module is completely unloaded from memory
	 */
	UNLOADED,

	/**
	 * Module encountered an error during operation
	 */
	ERROR,

	/**
	 * Module state cannot be determined
	 */
	UNKNOWN
}
