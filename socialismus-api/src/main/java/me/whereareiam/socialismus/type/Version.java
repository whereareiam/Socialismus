package me.whereareiam.socialismus.type;

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
	 * Minecraft versions from 1.16 to 26.1.2
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
	V_1_21_10,
	V_1_21_11,
	V_26_1,
	V_26_1_1,
	V_26_1_2;

	/**
	 * Converts a version string to its corresponding Version enum.
	 *
	 * @param version the version string (e.g., "1.16.5")
	 * @return the corresponding Version enum, or UNSUPPORTED if invalid
	 */
	public static Version of(String version) {
		if (version == null || version.isEmpty()) return Version.UNSUPPORTED;

		// Extract the leading numeric version components, ignoring revision/build
		// suffixes such as "-R0.1-SNAPSHOT" (Spigot/Bukkit) or ".build.63-stable"
		// (Paper's 26.1+ versioning scheme). For example,
		// "26.1.2.build.63-stable" -> [26, 1, 2].
		int[] currentParts = leadingNumericComponents(version.split("[\\s-]")[0]);
		if (currentParts.length == 0) return Version.UNSUPPORTED;

		StringBuilder name = new StringBuilder("V");
		for (int part : currentParts) {
			name.append("_").append(part);
		}

		try {
			return Version.valueOf(name.toString());
		} catch (IllegalArgumentException e) {
			Version latest = getLatest();
			if (latest == UNSUPPORTED) {
				return UNSUPPORTED;
			}

			int[] latestParts = versionComponents(latest);

			int minLength = Math.min(latestParts.length, currentParts.length);
			for (int i = 0; i < minLength; i++) {
				if (currentParts[i] > latestParts[i]) {
					return FUTURE;
				} else if (currentParts[i] < latestParts[i]) {
					return UNSUPPORTED;
				}
			}

			if (currentParts.length > latestParts.length) {
				return FUTURE;
			}

			return UNSUPPORTED;
		}
	}

	/**
	 * Extracts the leading run of numeric, dot-separated components from a raw
	 * version token, stopping at the first non-numeric segment. This strips
	 * build/revision suffixes (e.g. Paper's "26.1.2.build.63") so the underlying
	 * Minecraft version can be matched.
	 *
	 * @param version the raw version token (already trimmed of "-"/whitespace tails)
	 * @return the leading numeric components, or an empty array if none are present
	 */
	private static int[] leadingNumericComponents(String version) {
		String[] parts = version.split("\\.");
		int count = 0;
		while (count < parts.length && parts[count].matches("\\d+")) {
			count++;
		}

		int[] result = new int[count];
		for (int i = 0; i < count; i++) {
			result[i] = Integer.parseInt(parts[i]);
		}

		return result;
	}

	/**
	 * Gets the latest supported version.
	 *
	 * @return the latest Version enum, or UNSUPPORTED if no valid versions exist
	 */
	public static Version getLatest() {
		return Arrays.stream(Version.values())
				.filter(Version::isConcreteVersion)
				.max(Comparator.comparing(Version::versionComponents, Version::compareComponents))
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
		return compare(version1, version2) < 0;
	}

	/**
	 * Checks if version1 is higher than version2.
	 *
	 * @param version1 the first version to compare
	 * @param version2 the second version to compare
	 * @return true if version1 is higher than version2
	 */
	public static boolean isHigherThan(Version version1, Version version2) {
		return compare(version1, version2) > 0;
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

	private static int compare(Version version1, Version version2) {
		if (version1 == version2) {
			return 0;
		}

		if (version1 == FUTURE) {
			return 1;
		}

		if (version2 == FUTURE) {
			return -1;
		}

		boolean version1Concrete = isConcreteVersion(version1);
		boolean version2Concrete = isConcreteVersion(version2);

		if (version1Concrete && version2Concrete) {
			return compareComponents(versionComponents(version1), versionComponents(version2));
		}

		if (version1Concrete) {
			return 1;
		}

		if (version2Concrete) {
			return -1;
		}

		return Integer.compare(version1.ordinal(), version2.ordinal());
	}

	private static boolean isConcreteVersion(Version version) {
		return version.name().startsWith("V_");
	}

	private static int[] versionComponents(Version version) {
		return Arrays.stream(version.name().substring(2).split("_"))
				.mapToInt(Integer::parseInt)
				.toArray();
	}

	private static int compareComponents(int[] left, int[] right) {
		int minLength = Math.min(left.length, right.length);
		for (int i = 0; i < minLength; i++) {
			int comparison = Integer.compare(left[i], right[i]);
			if (comparison != 0) {
				return comparison;
			}
		}

		return Integer.compare(left.length, right.length);
	}
}
