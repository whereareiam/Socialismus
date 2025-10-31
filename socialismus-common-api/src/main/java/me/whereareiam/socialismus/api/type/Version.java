package me.whereareiam.socialismus.api.type;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Represents Minecraft server versions supported by the plugin.
 * Provides utility methods for version comparison and conversion.
 */
public enum Version {
	/**
	 * Represents an unknown or unrecognized version
	 */
	UNKNOWN,
	/**
	 * Represents an unsupported or invalid version
	 */
	UNSUPPORTED,

	/**
	 * Represents the future version, used for pre-release or upcoming versions
	 */
	FUTURE,

	/**
	 * Minecraft versions from 1.16 to 1.21.4
	 */
	V_1_16,
	V_1_16_1,
	V_1_16_2,
	V_1_16_3,
	V_1_16_4,
	V_1_16_5,
	V_1_17,
	V_1_17_1,
	V_1_18,
	V_1_18_1,
	V_1_18_2,
	V_1_19,
	V_1_19_1,
	V_1_19_2,
	V_1_19_3,
	V_1_19_4,
	V_1_20,
	V_1_20_1,
	V_1_20_2,
	V_1_20_3,
	V_1_20_4,
	V_1_20_5,
	V_1_20_6,
	V_1_21,
	V_1_21_1,
	V_1_21_2,
	V_1_21_3,
	V_1_21_4,
	V_1_21_5,
	V_1_21_6,
	V_1_21_7,
	V_1_21_8,
	V_1_21_9,
	V_1_21_10;

	/**
	 * Converts a version string to its corresponding Version enum.
	 *
	 * @param version the version string (e.g., "1.16.5")
	 * @return the corresponding Version enum, or UNSUPPORTED if invalid
	 */
	public static Version of(String version) {
		if (version == null || version.isEmpty()) return Version.UNSUPPORTED;

		String normalizedVersion = version.split("[\\s-]")[0];

		try {
			return Version.valueOf("V_" + normalizedVersion.replace(".", "_"));
		} catch (IllegalArgumentException e) {
			Version latest = getLatest();
			if (latest == UNSUPPORTED) {
				return UNSUPPORTED;
			}

			String[] latestVersionParts = latest.name().substring(2).split("_");
			String[] currentVersionParts = normalizedVersion.split("\\.");

			int minLength = Math.min(latestVersionParts.length, currentVersionParts.length);
			for (int i = 0; i < minLength; i++) {
				try {
					int latestPart = Integer.parseInt(latestVersionParts[i]);
					int currentPart = Integer.parseInt(currentVersionParts[i]);

					if (currentPart > latestPart) {
						return FUTURE;
					} else if (currentPart < latestPart) {
						return UNSUPPORTED;
					}
				} catch (NumberFormatException ex) {
					return UNSUPPORTED;
				}
			}

			if (currentVersionParts.length > latestVersionParts.length) {
				return FUTURE;
			}

			return UNSUPPORTED;
		}
	}

	/**
	 * Gets the latest supported version.
	 *
	 * @return the latest Version enum, or UNSUPPORTED if no valid versions exist
	 */
	public static Version getLatest() {
		return Arrays.stream(Version.values())
				.filter(version -> version != UNSUPPORTED && version != FUTURE)
				.max(Comparator.comparing(v -> v.name().substring(2).replaceAll("_", ".")))
				.orElse(UNSUPPORTED);
	}

	/**
	 * Checks if version1 is lower than version2.
	 *
	 * @param version1 the first version to compare
	 * @param version2 the second version to compare
	 * @return true if version1 is lower than version2
	 */
	public static boolean isLowerThan(Version version1, Version version2) {
		return version1.ordinal() < version2.ordinal();
	}

	/**
	 * Checks if version1 is higher than version2.
	 *
	 * @param version1 the first version to compare
	 * @param version2 the second version to compare
	 * @return true if version1 is higher than version2
	 */
	public static boolean isHigherThan(Version version1, Version version2) {
		return version1.ordinal() > version2.ordinal();
	}

	/**
	 * Checks if this version is at least the specified version.
	 *
	 * @param version the version to compare against
	 * @return true if this version is equal to or higher than the specified version
	 */
	public boolean isAtLeast(Version version) {
		return !isLowerThan(this, version);
	}
}