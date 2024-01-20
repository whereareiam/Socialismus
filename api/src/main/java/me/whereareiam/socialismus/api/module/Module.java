package me.whereareiam.socialismus.api.module;

/**
 * Module
 *
 * @since 1.2.0
 */
public interface Module {
	/**
	 * Initializes the module
	 *
	 * @since 1.2.0
	 */
	void initialize();

	/**
	 * Checks if the module is enabled
	 *
	 * @since 1.2.0
	 */
	boolean isEnabled();

	/**
	 * Reloads the module
	 *
	 * @since 1.2.0
	 */
	void reload();
}