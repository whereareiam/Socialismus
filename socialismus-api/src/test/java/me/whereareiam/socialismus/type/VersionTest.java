package me.whereareiam.socialismus.type;

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
	@DisplayName("Future versions should compare higher than supported releases")
	void futureVersionsCompareAboveSupportedVersions() {
		assertTrue(Version.isHigherThan(Version.FUTURE, Version.V_1_20_6));
		assertFalse(Version.isLowerThan(Version.FUTURE, Version.V_1_20_6));
		assertTrue(Version.FUTURE.isAtLeast(Version.V_1_20_6));
	}
}
