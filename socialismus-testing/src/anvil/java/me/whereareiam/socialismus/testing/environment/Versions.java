package me.whereareiam.socialismus.testing.environment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.anvil.api.model.process.Distribution;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * The Minecraft and Velocity versions a run of the journeys starts its networks at. The newest are the default;
 * the compatibility workflow runs suites at older ones with {@code -Psocialismus.journeys.minecraft=1.16.5} and
 * {@code -Psocialismus.journeys.velocity=4.0.0}. Anvil picks the Java each version needs.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Versions {
	/** The Minecraft version of every Paper and Spigot server. */
	public static final String MINECRAFT = System.getProperty("socialismus.journeys.minecraft", "26.3");
	/** The version of every Velocity proxy. */
	public static final String VELOCITY = System.getProperty("socialismus.journeys.velocity", "4.3.1");
	/** The first Minecraft version the PAPER jar and the bundle run on, since the Paper API they use needs Java 25. */
	public static final String PAPER_JAR = "26.1";

	// The Paper build of each version the journeys run; Anvil fetches it from PaperMC.
	private static final Map<String, String> PAPER_BUILDS = Map.of(
			"1.16.5", "794",
			"1.19.4", "550",
			"1.20.6", "151",
			"1.21.1", "133",
			"1.21.11", "132",
			"26.1.2", "74",
			"26.3", "249"
	);
	// The SHA-256 of each Spigot jar the journeys run, as GetBukkit serves it.
	private static final Map<String, String> SPIGOT_JARS = Map.of(
			"1.16.5", "e8db52306a7a465a48a4acf6e346664667dec3ed9f6521ba3087efd6892d004c",
			"1.19.4", "beb43043e8d03d1d4557755102aebc0bfe89368a594939fb1db5f9e5ee84ebb3",
			"1.20.6", "d9aab418f8404148b47c8faba484698a526f121f9fb5412264de9dc4b65db02f",
			"1.21.1", "dc5508243bc6cb3208314ee39057f7c1b18544ed133b2928cc56eff17095cabe",
			"1.21.11", "6481503fca2838776b3da5a3f1c030e1328abc2fd77d9ea1bb4814889b540dcd",
			"26.1.2", "95f871fd6d055ba10b5a058768ddad43b0be0286480c8eba435c835c95d5f19c",
			"26.3", "d64fb48abc0a02c09273487248b641005b363c5070860d97bc13a35c8808a4e4"
	);
	// The Velocity build of each version the journeys run.
	private static final Map<String, String> VELOCITY_BUILDS = Map.of(
			"4.0.0", "6",
			"4.3.1", "45"
	);

	/**
	 * Returns whether Paper servers of this run install the PAPER jar; older ones install the BUKKIT jar, as admins of
	 * those servers do.
	 */
	public static boolean paperJar() {
		return atLeast(PAPER_JAR);
	}

	/**
	 * Returns whether the servers of this run are at least the given Minecraft version.
	 *
	 * @param version a Minecraft version, such as {@code 1.19.4}
	 */
	public static boolean atLeast(@NotNull String version) {
		return compare(MINECRAFT, version) >= 0;
	}

	@NotNull
	static Distribution paper() {
		return Distribution.remote(MINECRAFT, known(PAPER_BUILDS, "Paper", MINECRAFT));
	}

	@NotNull
	static Distribution spigot() {
		return Distribution.pinned(MINECRAFT, known(SPIGOT_JARS, "Spigot", MINECRAFT));
	}

	@NotNull
	static Distribution velocity() {
		return Distribution.remote(VELOCITY, known(VELOCITY_BUILDS, "Velocity", VELOCITY));
	}

	private static String known(Map<String, String> builds, String platform, String version) {
		String build = builds.get(version);
		if (build == null)
			throw new IllegalArgumentException("The journeys pin no " + platform + " build of " + version + "; known: " + builds.keySet());

		return build;
	}

	/**
	 * Compares two versions number by number, so 1.21.11 is newer than 1.21.4 and 26.1 newer than both.
	 */
	static int compare(@NotNull String left, @NotNull String right) {
		String[] a = left.split("\\.");
		String[] b = right.split("\\.");
		for (int i = 0; i < Math.max(a.length, b.length); i++) {
			int difference = Integer.compare(i < a.length ? Integer.parseInt(a[i]) : 0, i < b.length ? Integer.parseInt(b[i]) : 0);
			if (difference != 0) return difference;
		}

		return 0;
	}
}
