package me.whereareiam.socialismus.module.bubbler.api;

import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.type.Version;

import java.util.Map;

/**
 * Utility for resolving version-specific values.
 * Helps packets find the correct index/type for the current protocol version.
 */
public final class VersionResolver {
	/**
	 * Resolves a value from a version map based on the current protocol version.
	 * <p>
	 * Picks the highest mapped version that is not newer than the running server,
	 * comparing semantically via {@link Version#isHigherThan}. Servers newer than
	 * anything the host knows are reported as {@link Version#FUTURE}, which ranks
	 * above every concrete version, so all mapped versions qualify and the latest
	 * one wins. This keeps forward-compatible protocol layouts (e.g. display
	 * entity metadata, unchanged in newer releases) resolving correctly without
	 * collapsing {@code FUTURE} to a concrete version.
	 *
	 * @param versionMap Map of version to value
	 * @param defaultValue Value to return if no matching version found
	 * @return The resolved value for the current version
	 */
	public static <T> T resolve(Map<Version, T> versionMap, T defaultValue) {
		Version server = Constants.SERVER_VERSION;
		Version bestVersion = null;
		for (Version candidate : versionMap.keySet()) {
			if (!Version.isHigherThan(candidate, server)
					&& (bestVersion == null || Version.isHigherThan(candidate, bestVersion))) {
				bestVersion = candidate;
			}
		}

		return bestVersion != null ? versionMap.get(bestVersion) : defaultValue;
	}

	/**
	 * Resolves a value from a version map, throwing if no mapping found.
	 */
	public static <T> T resolveOrThrow(Map<Version, T> versionMap) {
		T value = resolve(versionMap, null);
		if (value == null) {
			throw new IllegalStateException(
					"No mapping found for version " + Constants.SERVER_VERSION
			);
		}

		return value;
	}
}
