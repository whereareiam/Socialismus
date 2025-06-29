package me.whereareiam.socialismus.shared;

public final class Constants {
	public static final String NAME = "@name@";
	public static final String VERSION = "@version@";
	public static final String CHANNEL = "socialismus";

	public static final class BStats {
		public static final int BUKKIT_ID = 19855;
		public static final int VELOCITY_ID = 22720;
	}

	public static final class Dependency {
		public static final String GUICE = "@guiceVersion@";
		public static final String JACKSON = "@jacksonVersion@";
		public static final String SNAKEYAML = "@snakeyamlVersion@";
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
