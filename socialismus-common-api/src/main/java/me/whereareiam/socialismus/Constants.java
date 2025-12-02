package me.whereareiam.socialismus;

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
}
