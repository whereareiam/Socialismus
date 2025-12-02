package me.whereareiam.socialismus.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.configura.annotation.PostProcess;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.model.Event;

import java.util.Map;

/**
 * Main configuration settings class for the Socialismus plugin.
 * Contains all configurable options and their default values.
 *
 * <p>Configuration sections include:</p>
 * <ul>
 *   <li>Debug level and serialization settings</li>
 *   <li>Update checker configuration</li>
 *   <li>Miscellaneous plugin behaviors</li>
 *   <li>Event listener settings</li>
 * </ul>
 */
@Getter
@Setter
@ToString
public class Settings {
	/**
	 * Debug level for logging
	 */
	private int level;

	/**
	 * Serialization configuration for message formatting.
	 */
	private Serialization serialization;

	/**
	 * Synchronization settings for the plugin.
	 */
	private Synchronization synchronization;

	/**
	 * Update checker configuration
	 */
	private Updater updater;

	/**
	 * Command configuration
	 */
	private Commands commands;

	/**
	 * Miscellaneous plugin settings
	 */
	private Miscellaneous misc;

	/**
	 * Event listener configurations
	 */
	private Listeners listeners;

	@PostProcess
	public void applySynchronizationConstants() {
		if (synchronization == null) return;

		Constants.Synchronization.IDENTIFIER = synchronization.getServer();
		Constants.Synchronization.SYNCHRONIZATION = synchronization.isEnabled();
		Constants.Synchronization.CROSS_PLAYER_SYNC = synchronization.isCrossPlayerSync();
	}

	/**
	 * Synchronization settings for the plugin.
	 * Controls how the plugin synchronizes data and operations.
	 */
	@Getter
	@Setter
	@ToString
	public static class Synchronization {
		private boolean enabled;
		private String server;

		// Option for Proxy servers to use the real server name instead of custom identifier
		private boolean useRealServerName;
		private boolean crossPlayerSync;
	}

	/**
	 * Configuration for the plugin's updater checker.
	 * Controls updater notifications and checking behavior.
	 */
	@Getter
	@Setter
	@ToString
	public static class Updater {
		/**
		 * Whether to check for plugin updates
		 */
		private boolean checkForUpdates;

		/**
		 * Whether to show updater notifications
		 */
		private boolean warnAboutUpdates;

		/**
		 * Whether to warn about local builds
		 */
		private boolean warnAboutLocalBuilds;

		/**
		 * Whether to warn about development builds
		 */
		private boolean warnAboutDevBuilds;

		/**
		 * Update check interval in minutes
		 */
		private int interval;
	}

	/**
	 * Command configuration for the plugin.
	 */
	@Getter
	@Setter
	@ToString
	public static class Commands {
		/**
		 * Whether to use modern Brigadier-based command system (Paper 1.20.5+)
		 */
		private boolean useBrigadier;

		/**
		 * Whether to register asynchronous completions when available
		 */
		private boolean useAsyncCompletions;
	}

	/**
	 * Miscellaneous plugin configuration options.
	 * Controls various plugin behaviors and features.
	 */
	@Getter
	@Setter
	@ToString
	public static class Miscellaneous {
		/**
		 * Whether to disable join notifications
		 */
		private boolean disableJoinNotification;

		/**
		 * Whether to disable quit notifications
		 */
		private boolean disableQuitNotification;

		/**
		 * Whether to allow legacy message parsing
		 */
		private boolean allowLegacyParsing;

		/**
		 * Whether to use vanilla message sending
		 */
		private boolean vanillaSending;
	}

	/**
	 * Configuration for event listeners.
	 * Maps event names to their corresponding event configurations.
	 */
	@Getter
	@Setter
	@ToString
	public static class Listeners {
		/**
		 * Map of event name to event configuration
		 */
		private Map<String, Event> events;
	}

	/**
	 * Serialization configuration for message formatting.
	 * Controls how messages are serialized and formatted.
	 */
	@Getter
	@Setter
	@ToString
	public static class Serialization {
		/**
		 * Serializer adapter ID/type to use.
		 * Available options: "MINIMESSAGE", "GSON", "LEGACY_AMPERSAND", "LEGACY_SECTION", "PLAIN"
		 * Default: "MINIMESSAGE"
		 */
		private String type = "MINIMESSAGE";

		/**
		 * Whether to enable legacy color code parsing (& and § codes).
		 * When enabled, legacy codes in input will be converted to the target adapter format.
		 * Default: false
		 */
		private boolean enableLegacyColors = false;
	}
}