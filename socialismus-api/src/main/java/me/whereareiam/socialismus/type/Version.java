package me.whereareiam.socialismus.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents Minecraft server versions supported by the plugin.
 * Provides utility methods for version parsing and comparison.
 */
public enum Version {
	/**
	 * Represents an unknown or unresolved server version.
	 */
	UNKNOWN,

	/**
	 * Represents an unsupported or invalid server version.
	 */
	UNSUPPORTED,

	/**
	 * Represents a server version newer than the latest version known by the plugin.
	 */
	FUTURE,

	/**
	 * Concrete Minecraft versions currently recognized by the plugin.
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
	V_26_1_2,
	V_26_2;

	private static final Map<String, Version> CONCRETE_VERSIONS;
	private static final @NotNull Version LATEST;

	private final int[] components;
	private final String release;

	Version() {
		if (!name().startsWith("V_")) {
			components = new int[0];
			release = null;
			return;
		}

		release = name().substring(2).replace('_', '.');
		components = parseReleaseComponents(release);
	}

	static {
		Map<String, Version> concreteVersions = new LinkedHashMap<>();
		Version latest = UNSUPPORTED;

		for (Version version : values()) {
			if (!version.isConcreteVersion()) continue;
			concreteVersions.put(version.release, version);

			if (latest == UNSUPPORTED || compareConcreteVersions(version, latest) > 0) {
				latest = version;
			}
		}

		CONCRETE_VERSIONS = Map.copyOf(concreteVersions);
		LATEST = latest;
	}

	/**
	 * Resolves a raw server version string to a known {@link Version}.
	 *
	 * <p>The parser accepts plain versions such as {@code 1.21.4} and server build
	 * strings such as {@code 1.21.4-R0.1-SNAPSHOT} or
	 * {@code 26.1.2.build.63-stable}. Unknown versions newer than the latest
	 * supported release are classified as {@link #FUTURE}; older or malformed
	 * values are classified as {@link #UNSUPPORTED}.</p>
	 *
	 * @param version the raw version string to parse
	 * @return the resolved version classification
	 */
	public static @NotNull Version of(@Nullable String version) {
		if (version == null) return UNSUPPORTED;

		String normalized = version.trim();
		if (normalized.isEmpty()) return UNSUPPORTED;

		int[] parsedComponents = parseLeadingComponents(normalized);
		if (parsedComponents.length == 0) return UNSUPPORTED;

		Version resolved = CONCRETE_VERSIONS.get(joinComponents(parsedComponents));
		if (resolved != null) return resolved;
		if (!LATEST.isConcreteVersion()) return UNSUPPORTED;

		return compareComponents(parsedComponents, LATEST.components) > 0
				? FUTURE
				: UNSUPPORTED;
	}

	/**
	 * Returns the latest supported concrete version.
	 *
	 * @return the latest supported version, or {@link #UNSUPPORTED} if no concrete
	 * versions are registered
	 */
	public static @NotNull Version getLatest() {
		return LATEST;
	}

	/**
	 * Determines whether one version is lower than another.
	 *
	 * @param version1 the version to test
	 * @param version2 the version to compare against
	 * @return {@code true} when {@code version1} is lower than {@code version2}
	 */
	public static boolean isLowerThan(@NotNull Version version1, @NotNull Version version2) {
		return compare(version1, version2) < 0;
	}

	/**
	 * Determines whether one version is higher than another.
	 *
	 * @param version1 the version to test
	 * @param version2 the version to compare against
	 * @return {@code true} when {@code version1} is higher than {@code version2}
	 */
	public static boolean isHigherThan(@NotNull Version version1, @NotNull Version version2) {
		return compare(version1, version2) > 0;
	}

	/**
	 * Determines whether this version is at least the supplied version.
	 *
	 * @param version the minimum version to compare against
	 * @return {@code true} when this version is equal to or higher than {@code version}
	 */
	public boolean isAtLeast(@NotNull Version version) {
		return compare(this, version) >= 0;
	}

	private boolean isConcreteVersion() {
		return release != null;
	}

	private static int[] parseLeadingComponents(String version) {
		String[] parts = version.split("[\\s-]", 2)[0].split("\\.");
		int count = 0;

		while (count < parts.length && isNumeric(parts[count])) {
			count++;
		}

		int[] parsed = new int[count];
		for (int i = 0; i < count; i++) {
			parsed[i] = Integer.parseInt(parts[i]);
		}

		return parsed;
	}

	private static boolean isNumeric(String value) {
		if (value.isEmpty()) {
			return false;
		}

		for (int i = 0; i < value.length(); i++) {
			if (!Character.isDigit(value.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	private static String joinComponents(int[] components) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < components.length; i++) {
			if (i > 0) {
				builder.append('.');
			}

			builder.append(components[i]);
		}

		return builder.toString();
	}

	private static int[] parseReleaseComponents(String release) {
		String[] parts = release.split("\\.");
		int[] parsed = new int[parts.length];
		for (int i = 0; i < parts.length; i++) {
			parsed[i] = Integer.parseInt(parts[i]);
		}

		return parsed;
	}

	private static int compare(@NotNull Version left, @NotNull Version right) {
		if (left == right) return 0;
		if (left == FUTURE) return 1;
		if (right == FUTURE) return -1;
		if (left.isConcreteVersion()) return right.isConcreteVersion()
				? compareConcreteVersions(left, right)
				: 1;

		if (right.isConcreteVersion()) return -1;

		return Integer.compare(left.ordinal(), right.ordinal());
	}

	private static int compareConcreteVersions(@NotNull Version version1, @NotNull Version version2) {
		return compareComponents(version1.components, version2.components);
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
