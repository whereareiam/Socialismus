package me.whereareiam.socialismus.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * This class is used to get the instance of the plugin.
 */
@Singleton
public class SocialismusAPI {
	private static Socialismus instance;

	@Inject
	public SocialismusAPI(Socialismus instance) {
		SocialismusAPI.instance = instance;
	}

	/**
	 * Gets the instance of the plugin
	 *
	 * @return The instance of the plugin
	 */
	public static Socialismus getInstance() {
		return instance;
	}
}
