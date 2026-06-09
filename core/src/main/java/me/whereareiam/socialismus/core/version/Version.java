package me.whereareiam.socialismus.core.version;

import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public enum Version {
		V1_19_4("1.19.4", 0),
		V1_20_0("1.20", 0),
		V1_20_1("1.20.1", 0),
		V1_20_2("1.20.2", 1),
		V1_20_3("1.20.3", 1),
		V1_20_4("1.20.4", 1),
		V1_20_5("1.20.5", 1),
		V1_20_6("1.20.6", 1),
		V1_21("1.21", 1),
		V1_21_1("1.21.1", 1),
		V1_21_2("1.21.2", 1),
		V1_21_3("1.21.3", 1),
		V1_21_4("1.21.4", 1),
		V1_21_5("1.21.5", 1),
		V1_21_6("1.21.6", 1),
		V1_21_7("1.21.7", 1),
		V1_21_8("1.21.8", 1),
		V1_21_9("1.21.9", 1),
		V1_21_10("1.21.10", 1),
		V1_21_11("1.21.11", 1),
		V26_1("26.1", 1),
		V26_1_1("26.1.1", 1),
		V26_1_2("26.1.2", 1),
		FUTURE("future", 1);

		private static final Map<String, Version> VERSION_MAP = new HashMap<>();
		private final String versionString;

		Version(String versionString, int id) {
				this.versionString = versionString;
		}

		static {
				for (Version v : values()) {
						VERSION_MAP.put(v.versionString, v);
				}
		}

		public static Version getVersion() {
				String detailedVersion = Bukkit.getBukkitVersion();
				String version = detailedVersion.split("-")[0];
				Version result = VERSION_MAP.get(version);

				if (result == null) {
						if (isFutureVersion(version)) {
								return FUTURE;
						}
						throw new UnsupportedOperationException("Unsupported server version: " + detailedVersion);
				}
				return result;
		}

		private static boolean isFutureVersion(String version) {
				try {
						int[] currentVersion = versionComponents(version);
						Version latestSupportedVersion = getLatestSupportedVersionForTrack(currentVersion[0]);

						if (latestSupportedVersion == null) {
								return false;
						}

						return compareComponents(currentVersion, versionComponents(latestSupportedVersion.versionString)) > 0;
				} catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
				}
				return false;
		}

		private static Version getLatestSupportedVersionForTrack(int majorVersion) {
				return Arrays.stream(values())
						.filter(version -> version != FUTURE)
						.filter(version -> isSameVersionTrack(version, majorVersion))
						.max(Comparator.comparing(version -> versionComponents(version.versionString), Version::compareComponents))
						.orElse(null);
		}

		private static boolean isSameVersionTrack(Version version, int majorVersion) {
				int supportedMajorVersion = versionComponents(version.versionString)[0];
				return majorVersion == 1 ? supportedMajorVersion == 1 : supportedMajorVersion > 1;
		}

		private static int[] versionComponents(String version) {
				return Arrays.stream(version.split("\\."))
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
