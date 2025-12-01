package me.whereareiam.socialismus;

import me.whereareiam.socialismus.type.Version;

public final class Constants {
	public static final String NAME = "@name@";
	public static final String VERSION = "@version@";

	public static Version SERVER_VERSION = Version.UNKNOWN;

	public static final class Channels {
		public static final String NAME = "socialismus";

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
		public static final String GUICE = "@guiceVersion@";
		public static final String CONFIGURA = "@configuraVersion@";
		public static final String JEDIS = "@jedisVersion@";
		public static final String ADVENTURE = "@adventureVersion@";
		public static final String ADVENTURE_BUKKIT = "@adventureBukkitVersion@";

		public static final String CLOUD = "@cloudVersion@";
		public static final String CLOUD_COOLDOWN = "@cloudCooldownVersion@";
		public static final String CLOUD_PAPER = "@cloudPaperVersion@";
		public static final String CLOUD_VELOCITY = "@cloudVelocityVersion@";
		public static final String CLOUD_MINECRAFT_EXTRAS = "@cloudMinecraftExtrasVersion@";
	}
}
