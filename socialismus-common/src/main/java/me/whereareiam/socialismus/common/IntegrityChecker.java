package me.whereareiam.socialismus.common;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.Constants;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.type.PlatformType;
import me.whereareiam.socialismus.api.type.PluginType;
import me.whereareiam.socialismus.api.type.Version;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class IntegrityChecker {
	public boolean checkIntegrity() {
		PluginType pluginType = PluginType.getExactType();
		Version currentVersion = Constants.SERVER_VERSION;

		return switch (pluginType) {
			case PAPER -> checkPaperIntegrity(currentVersion);
			case BUKKIT -> checkBukkitIntegrity(currentVersion);
			default -> false;
		};
	}

	private boolean checkPaperIntegrity(Version currentVersion) {
		if (Version.isLowerThan(currentVersion, Version.V_1_20_1)) {
			Logger.severe("You can't use PAPER version of the plugin, because it is made for versions greater than or equal to 1.20.1" +
					" and your version is " + currentVersion);

			return false;
		}

		return Version.isHigherThan(currentVersion, Version.V_1_20_1);
	}

	private boolean checkBukkitIntegrity(Version currentVersion) {
		if (currentVersion.isAtLeast(Version.V_1_20_1)
				&& (PlatformType.getType() == PlatformType.FOLIA || PlatformType.getType() == PlatformType.PAPER)) {
			Logger.warn("It seems that you are using a version of the plugin that is not recommended for this platform. " +
					"Please consider using the PAPER version of the plugin with Java 21+, it will be more stable and performant.");

			return true;
		}

		return currentVersion.isAtLeast(Version.V_1_16);
	}
}
