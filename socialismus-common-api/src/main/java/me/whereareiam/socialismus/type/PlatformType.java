package me.whereareiam.socialismus.type;

/**
 * Enumeration representing different Minecraft server platform types.
 * This enum provides methods to detect and compare various server implementations
 * such as Bukkit, Spigot, Paper, Folia, and Velocity.
 */
public enum PlatformType {
	/**
	 * Represents the basic Bukkit server platform
	 */
	BUKKIT,
	/**
	 * Represents the Spigot server platform, an enhanced version of Bukkit
	 */
	SPIGOT,
	/**
	 * Represents the Paper server platform, a high-performance fork of Spigot
	 */
	PAPER,
	/**
	 * Represents the Folia server platform, a multi-threaded fork of Paper
	 */
	FOLIA,
	/**
	 * Represents the Velocity proxy server platform
	 */
	VELOCITY,
	/**
	 * Represents an unknown or unsupported platform type
	 */
	UNKNOWN;

	/**
	 * Determines the current platform type by checking the presence of platform-specific classes.
	 *
	 * @return The detected {@link PlatformType} based on the current environment
	 */
	public static PlatformType getType() {
		if (isVelocity())
			return VELOCITY;
		if (isFolia())
			return FOLIA;
		if (isPaper())
			return PAPER;
		if (isSpigot())
			return SPIGOT;
		if (isBukkit())
			return BUKKIT;

		return UNKNOWN;
	}

	/**
	 * Checks if the current platform is a proxy server.
	 *
	 * @return true if the platform is Velocity, false otherwise
	 */
	public static boolean isProxy() {
		return isVelocity();
	}

	/**
	 * Checks if the current platform is a game server (Bukkit, Spigot, Paper, or Folia).
	 *
	 * @return true if the platform is a game server, false otherwise
	 */
	public static boolean isGameServer() {
		return isBukkit() || isSpigot() || isPaper() || isFolia();
	}

	/**
	 * Checks if the current platform is at least as advanced as the specified platform.
	 * The hierarchy is: Bukkit -> Spigot -> Paper -> Folia
	 *
	 * @param platform The platform type to compare against
	 * @return true if the current platform is at least as advanced as the specified platform
	 */
	public static boolean isAtLeast(PlatformType platform) {
		return switch (platform) {
			case BUKKIT -> isBukkit() || isSpigot() || isPaper() || isFolia();
			case SPIGOT -> isSpigot() || isPaper() || isFolia();
			case PAPER -> isPaper() || isFolia();
			case FOLIA -> isFolia();
			default -> false;
		};
	}

	/**
	 * Checks if the platform is running Velocity.
	 *
	 * @return true if Velocity is detected, false otherwise
	 */
	private static boolean isVelocity() {
		return isClassPresent("com.velocitypowered.api.plugin.Plugin");
	}

	/**
	 * Checks if the platform is running Folia.
	 *
	 * @return true if Folia is detected, false otherwise
	 */
	private static boolean isFolia() {
		return isClassPresent("io.papermc.paper.threadedregions.ThreadedRegionizer");
	}

	/**
	 * Checks if the platform is running Paper.
	 *
	 * @return true if Paper is detected, false otherwise
	 */
	private static boolean isPaper() {
		return isClassPresent("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
	}

	/**
	 * Checks if the platform is running Spigot.
	 *
	 * @return true if Spigot is detected, false otherwise
	 */
	private static boolean isSpigot() {
		return isClassPresent("org.spigotmc.SpigotConfig");
	}

	/**
	 * Checks if the platform is running Bukkit.
	 *
	 * @return true if Bukkit is detected, false otherwise
	 */
	private static boolean isBukkit() {
		return isClassPresent("org.bukkit.Bukkit");
	}

	/**
	 * Utility method to check if a specific class is present in the classpath.
	 *
	 * @param className The fully qualified name of the class to check
	 * @return true if the class is present, false otherwise
	 */
	private static boolean isClassPresent(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		} catch (NoClassDefFoundError e) {
			return className.equals("org.bukkit.plugin.java.JavaPlugin");
		}
	}
}