package me.whereareiam.socialismus.module.bubbler.common;

import me.whereareiam.socialismus.model.player.PlayerDataKey;
import me.whereareiam.socialismus.model.requirement.RequirementKey;
import me.whereareiam.socialismus.module.bubbler.api.model.requirement.ActivatorRequirement;
import me.whereareiam.socialismus.module.bubbler.api.type.ActivatorType;

/**
 * Constants for the Bubbler module.
 * Follows the same pattern as Socialismus core Constants class.
 */
public final class BubblerConstants {
	/**
	 * Player data keys specific to Bubbler module.
	 */
	public static final class DataKeys {
		/**
		 * The last activator type that triggered a bubble for the player.
		 */
		public static final PlayerDataKey<ActivatorType> LAST_ACTIVATOR =
				PlayerDataKey.create("bubbler", "last_activator", ActivatorType.class);
	}

	/**
	 * Requirement keys specific to Bubbler module.
	 * These extend the requirement system without modifying Socialismus core.
	 */
	public static final class Requirements {
		/**
		 * Activator-based requirements (e.g., bubble was activated via CHAT or COMMAND)
		 */
		public static final RequirementKey<ActivatorRequirement> ACTIVATOR =
				RequirementKey.create("bubbler", "activator", ActivatorRequirement.class);
	}

	private BubblerConstants() {
		// Utility class
	}
}
