package me.whereareiam.socialismus.api.type;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Represents Minecraft server versions supported by the plugin.
 * Provides utility methods for version comparison and conversion.
 */
public enum Version {
	/**
	 * Represents an unsupported or invalid version
	 */
	UNSUPPORTED,

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
	V_1_21_5;

	/**
	 * Converts a version string to its corresponding Version enum.
	 *
	 * @param version the version string (e.g., "1.16.5")
	 * @return the corresponding Version enum, or UNSUPPORTED if invalid
	 */
	public static Version of(String version) {
		if (version == null || version.isEmpty()) return Version.UNSUPPORTED;

		String normalizedVersion = version.split("[\\s-]")[0].replace(".", "_");

		try {
			return Version.valueOf("V_" + normalizedVersion);
		} catch (IllegalArgumentException e) {
			return Version.UNSUPPORTED;
		}
	}

	/**
	 * Gets the latest supported version.
	 *
	 * @return the latest Version enum, or UNSUPPORTED if no valid versions exist
	 */
	public static Version getLatest() {
		return Arrays.stream(Version.values())
				.filter(version -> version != UNSUPPORTED)
				.max(Comparator.comparing(Enum::name))
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