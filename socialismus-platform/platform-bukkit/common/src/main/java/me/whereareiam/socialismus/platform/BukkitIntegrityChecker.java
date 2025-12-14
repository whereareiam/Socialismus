package me.whereareiam.socialismus.platform;

import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.type.PlatformType;
import me.whereareiam.socialismus.type.PluginType;
import me.whereareiam.socialismus.type.Version;

import java.util.logging.Logger;

public class BukkitIntegrityChecker {
	public static boolean checkIntegrity(Logger logger) {
		PluginType pluginType = PluginType.getExactType();
		Version currentVersion = Constants.SERVER_VERSION;

		return switch (pluginType) {
			case PAPER -> checkPaperIntegrity(logger, currentVersion);
			case BUKKIT -> checkBukkitIntegrity(logger, currentVersion);
			default -> false;
		};
	}

	private static boolean checkPaperIntegrity(Logger logger, Version currentVersion) {
		if (Version.isLowerThan(currentVersion, Version.V_1_20_6)) {
			logger.severe("You can't use PAPER version of the plugin, because it is made for versions greater than or equal to 1.20.6" +
					" and your version is " + currentVersion);

			return false;
		}

		return Version.isHigherThan(currentVersion, Version.V_1_20_6);
	}

	private static boolean checkBukkitIntegrity(Logger logger, Version currentVersion) {
		if (currentVersion.isAtLeast(Version.V_1_20_6)
				&& (PlatformType.getType() == PlatformType.FOLIA || PlatformType.getType() == PlatformType.PAPER)) {
			logger.warning("It seems that you are using a version of the plugin that is not recommended for this platform. " +
					"Please consider using the PAPER version of the plugin with Java 21+, it will be more stable and performant.");

			return true;
		}

		return currentVersion.isAtLeast(Version.V_1_16);
	}
}
