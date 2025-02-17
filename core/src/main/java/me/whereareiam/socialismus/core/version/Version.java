package me.whereareiam.socialismus.core.version;

import org.bukkit.Bukkit;

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
						if (version.startsWith("1.")) {
								String[] parts = version.split("\\.");
								int minorVersion = Integer.parseInt(parts[1]);
								return minorVersion > 21 || (minorVersion == 21 && parts.length > 2);
						}
				} catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
				}
				return false;
		}
}