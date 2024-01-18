package me.whereareiam.socialismus.core.platform;

import com.google.inject.Singleton;

@Singleton
public class PlatformIdentifier {
	private static final boolean isFolia = isClassPresent("io.papermc.paper.threadedregions.ThreadedRegionizer");
	private static final boolean isPaper = isClassPresent("com.destroystokyo.paper.PaperConfig");
	private static final boolean isSpigot = isClassPresent("org.spigotmc.SpigotConfig");

	private static boolean isClassPresent(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static boolean isFolia() {
		return isFolia;
	}

	public static boolean isPaper() {
		return isPaper;
	}

	public static boolean isSpigot() {
		return isSpigot;
	}
}
