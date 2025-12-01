package me.whereareiam.socialismus.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.configura.annotation.PostProcess;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.model.Event;
import me.whereareiam.socialismus.type.SerializationType;

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
	 * Type of serialization to use for messages
	 */
	private SerializationType serializer;

	/**
	 * Synchronization settings for the plugin.
	 */
	private Synchronization synchronization;

	/**
	 * Update checker configuration
	 */
	private Updater updater;

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
		 * Whether to enable Brigadier command system
		 */
		private boolean allowBrigadierCommands;

		/**
		 * Whether to use vanilla message sending
		 */
		private boolean vanillaSending;

		/**
		 * Number of commands to display per page
		 */
		private int commandsPerPage;
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
}