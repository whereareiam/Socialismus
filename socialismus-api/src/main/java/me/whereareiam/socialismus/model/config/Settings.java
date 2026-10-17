package me.whereareiam.socialismus.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.model.Event;

import java.util.Map;

/**
 * The core settings of Socialismus, {@code settings} in its data directory: the log level, how messages are
 * serialized, what Socialismus changes about the server's own behaviour, and how it listens to events. Sync, the
 * updater, commands and chats have documents of their own.
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
	 * What Socialismus changes about the server's own behaviour.
	 */
	private Vanilla vanilla;

	/**
	 * Event listener configurations
	 */
	private Listeners listeners;

	/**
	 * The server's own behaviour that Socialismus can switch.
	 */
	@Getter
	@Setter
	@ToString
	public static class Vanilla {
		/**
		 * Whether the server's own message about a joining player is shown.
		 */
		private boolean joinMessage;

		/**
		 * Whether the server's own message about a leaving player is shown.
		 */
		private boolean quitMessage;

		/**
		 * Whether a formatted chat message is handed back to the server's chat event, so other plugins see it,
		 * instead of being sent by Socialismus. Velocity cannot take a message back, so Socialismus sends it there.
		 */
		private boolean sending;
	}

	/**
	 * Event listener settings.
	 */
	@Getter
	@Setter
	@ToString
	public static class Listeners {
		/**
		 * Map of event names to their configurations
		 */
		private Map<String, Event> events;
	}

	/**
	 * Serialization settings for messages.
	 */
	@Getter
	@Setter
	@ToString
	public static class Serialization {
		/**
		 * The serializer adapter to use for message formatting.
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
