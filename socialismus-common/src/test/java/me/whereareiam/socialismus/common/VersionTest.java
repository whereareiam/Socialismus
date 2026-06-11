package me.whereareiam.socialismus.common;

import me.whereareiam.socialismus.type.Version;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Version Tests")
class VersionTest {
	@Test
	@DisplayName("Should keep the latest supported release when patch numbers reach double digits")
	void getLatestUsesSemanticVersionOrdering() {
		assertEquals(Version.V_26_1_2, Version.getLatest());
	}

	@Test
	@DisplayName("Should resolve the new Minecraft version format to concrete enum values")
	void ofResolvesNewMinecraftVersionFormat() {
		assertEquals(Version.V_26_1, Version.of("26.1"));
		assertEquals(Version.V_26_1_1, Version.of("26.1.1"));
		assertEquals(Version.V_26_1_2, Version.of("26.1.2"));
	}

	@Test
	@DisplayName("Should treat newer new-format Minecraft versions as future versions")
	void ofRecognizesFutureMinecraftVersionFormats() {
		assertEquals(Version.FUTURE, Version.of("26.1.3"));
		assertEquals(Version.FUTURE, Version.of("26.2"));
	}

	@Test
	@DisplayName("Should strip Bukkit/Paper build suffixes when resolving versions")
	void ofStripsServerBuildSuffixes() {
		// Paper 26.1+ getBukkitVersion(), e.g. "26.1.2.build.63-stable"
		assertEquals(Version.V_26_1_2, Version.of("26.1.2.build.63-stable"));
		// Legacy Spigot/Bukkit getBukkitVersion() format
		assertEquals(Version.V_1_21_4, Version.of("1.21.4-R0.1-SNAPSHOT"));
		assertEquals(Version.V_26_1, Version.of("26.1.build.1-stable"));
		// A genuinely newer release with a build suffix is still in the future
		assertEquals(Version.FUTURE, Version.of("26.1.3.build.1-stable"));
	}

	@Test
	@DisplayName("Future versions should compare higher than supported releases")
	void futureVersionsCompareAboveSupportedVersions() {
		assertTrue(Version.isHigherThan(Version.FUTURE, Version.V_1_20_6));
		assertFalse(Version.isLowerThan(Version.FUTURE, Version.V_1_20_6));
		assertTrue(Version.FUTURE.isAtLeast(Version.V_1_20_6));
	}
}
