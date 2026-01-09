package me.whereareiam.socialismus;

import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatTrigger;
import me.whereareiam.socialismus.model.player.PlayerDataKey;
import me.whereareiam.socialismus.model.requirement.RequirementKey;
import me.whereareiam.socialismus.model.requirement.type.*;
import me.whereareiam.socialismus.type.Version;

public final class Constants {
	public static final String NAME = BuildConfig.NAME;
	public static final String VERSION = BuildConfig.VERSION;

	public static Version SERVER_VERSION = Version.UNKNOWN;

	public static final class Channels {
		public static final String NAME = BuildConfig.NAME.toLowerCase();

		public static final String CHAT = NAME + ":chat";
		public static final String CHAT_HISTORY = NAME + ":chat-history";
		public static final String PLAYERS = NAME + ":players";
	}

	public static final class Synchronization {
		public static String IDENTIFIER = "";
		public static Boolean SYNCHRONIZATION;
		public static Boolean CROSS_PLAYER_SYNC;
	}

	public static final class BStats {
		public static final int BUKKIT_ID = 19855;
		public static final int VELOCITY_ID = 22720;
	}

	public static final class Dependency {
		public static final String GUICE = BuildConfig.GUICE;
		public static final String CONFIGURA = BuildConfig.CONFIGURA;
		public static final String KEYSTONE = BuildConfig.KEYSTONE;
		public static final String COMMANDANT = BuildConfig.COMMANDANT;
		public static final String JEDIS = BuildConfig.JEDIS;
		public static final String ADVENTURE = BuildConfig.ADVENTURE_MINIMESSAGE;
		public static final String ADVENTURE_BUKKIT = BuildConfig.ADVENTURE_PLATFORM_BUKKIT;

		public static final String CLOUD = BuildConfig.CLOUD_CORE;
		public static final String CLOUD_COOLDOWN = BuildConfig.CLOUD_COOLDOWN;
		public static final String CLOUD_PAPER = BuildConfig.CLOUD_PAPER;
		public static final String CLOUD_VELOCITY = BuildConfig.CLOUD_VELOCITY;
		public static final String CLOUD_MINECRAFT_EXTRAS = BuildConfig.CLOUD_MINECRAFT_EXTRAS;
		public static final String BRIGADIER = BuildConfig.BRIGADIER;
	}

	public static final class DataKeys {
		/**
		 * The last chat channel the player used.
		 */
		public static final PlayerDataKey<Chat> LAST_CHAT =
				PlayerDataKey.create("socialismus", "last_chat", Chat.class);

		/**
		 * The last trigger that activated a chat for the player.
		 */
		public static final PlayerDataKey<ChatTrigger> LAST_TRIGGER =
				PlayerDataKey.create("socialismus", "last_trigger", ChatTrigger.class);

		/**
		 * The last message content the player sent.
		 */
		public static final PlayerDataKey<String> LAST_MESSAGE =
				PlayerDataKey.create("socialismus", "last_message", String.class);
	}

	public static final class Requirements {
		/**
		 * Placeholder-based requirements (e.g., %player_level% > 10)
		 */
		public static final RequirementKey<PlaceholderRequirement> PLACEHOLDER =
				RequirementKey.create("socialismus", "placeholder", PlaceholderRequirement.class);

		/**
		 * Permission-based requirements (e.g., player has "chat.local")
		 */
		public static final RequirementKey<PermissionRequirement> PERMISSION =
				RequirementKey.create("socialismus", "permission", PermissionRequirement.class);

		/**
		 * Server-based requirements (e.g., player is on "lobby" server)
		 */
		public static final RequirementKey<ServerRequirement> SERVER =
				RequirementKey.create("socialismus", "server", ServerRequirement.class);

		/**
		 * World-based requirements (e.g., player is in "world_nether")
		 */
		public static final RequirementKey<WorldRequirement> WORLD =
				RequirementKey.create("socialismus", "world", WorldRequirement.class);

		/**
		 * Chat-based requirements (e.g., last chat was "local")
		 */
		public static final RequirementKey<ChatRequirement> CHAT =
				RequirementKey.create("socialismus", "chat", ChatRequirement.class);

		/**
		 * Trigger-based requirements (e.g., chat was triggered by SYMBOL)
		 */
		public static final RequirementKey<TriggerRequirement> TRIGGER =
				RequirementKey.create("socialismus", "trigger", TriggerRequirement.class);

		/**
		 * Message-based requirements (e.g., message is not empty)
		 */
		public static final RequirementKey<MessageRequirement> MESSAGE =
				RequirementKey.create("socialismus", "message", MessageRequirement.class);
	}
}
