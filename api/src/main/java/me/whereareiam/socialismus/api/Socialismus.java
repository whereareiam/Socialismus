package me.whereareiam.socialismus.api;

/**
 * The main class of the Socialismus API.
 * <p>
 * This class contains methods for interacting with the plugin.
 * </p>
 * <p>
 * You can get an instance of this class by using {@link SocialismusAPI#getInstance()} ()}.
 * </p>
 * <p>
 * <b>Example:</b>
 * </p>
 * <pre>
 *     Socialismus socialismus = SocialismusAPI.getInstance();
 * </pre>
 *
 * @since 1.2.0
 */
public interface Socialismus {
	/**
	 * Reloads the plugin configuration and all modules.
	 *
	 * @since 1.2.0
	 */
	void reload();
}
