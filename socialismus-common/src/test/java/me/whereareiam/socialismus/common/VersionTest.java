package me.whereareiam.socialismus.common;

import me.whereareiam.socialismus.type.Version;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Version Tests")
class VersionTest {
	@Test
	@DisplayName("Should keep the latest supported release in semantic order")
	void getLatestUsesSemanticVersionOrdering() {
		assertEquals(Version.V_26_2, Version.getLatest());
	}

	@Test
	@DisplayName("Should resolve supported plain version strings")
	void ofResolvesSupportedPlainVersions() {
		assertResolvesTo("1.16", Version.V_1_16);
		assertResolvesTo("1.21.4", Version.V_1_21_4);
		assertResolvesTo("26.1", Version.V_26_1);
		assertResolvesTo("26.1.1", Version.V_26_1_1);
		assertResolvesTo("26.1.2", Version.V_26_1_2);
		assertResolvesTo("26.2", Version.V_26_2);
	}

	@Test
	@DisplayName("Should resolve supported version strings with server build suffixes")
	void ofResolvesSupportedVersionsWithBuildSuffixes() {
		assertResolvesTo("1.21.4-R0.1-SNAPSHOT", Version.V_1_21_4);
		assertResolvesTo("26.1.build.1-stable", Version.V_26_1);
		assertResolvesTo("26.1.2.build.63-stable", Version.V_26_1_2);
		assertResolvesTo("26.2.build.1-stable", Version.V_26_2);
		assertResolvesTo(" 26.1.2.build.63-stable ", Version.V_26_1_2);
	}

	@Test
	@DisplayName("Should classify malformed and older versions as unsupported")
	void ofRejectsUnsupportedVersions() {
		assertResolvesTo(null, Version.UNSUPPORTED);
		assertResolvesTo("", Version.UNSUPPORTED);
		assertResolvesTo("   ", Version.UNSUPPORTED);
		assertResolvesTo("abc", Version.UNSUPPORTED);
		assertResolvesTo("1.15.2", Version.UNSUPPORTED);
		assertResolvesTo("26.0.9", Version.UNSUPPORTED);
		assertResolvesTo("26.1.3", Version.UNSUPPORTED);
		assertResolvesTo("26.1.2.1", Version.UNSUPPORTED);
	}

	@Test
	@DisplayName("Should classify unknown newer versions as future")
	void ofRecognizesFutureVersions() {
		assertResolvesTo("26.2.1", Version.FUTURE);
		assertResolvesTo("26.2.1.build.1-stable", Version.FUTURE);
		assertResolvesTo("26.3", Version.FUTURE);
		assertResolvesTo("27.0", Version.FUTURE);
	}

	@Test
	@DisplayName("Future versions should compare above supported releases")
	void futureVersionsCompareAboveSupportedVersions() {
		assertTrue(Version.isHigherThan(Version.FUTURE, Version.V_1_20_6));
		assertFalse(Version.isLowerThan(Version.FUTURE, Version.V_1_20_6));
		assertTrue(Version.FUTURE.isAtLeast(Version.V_1_20_6));
	}

	@Test
	@DisplayName("Concrete versions should compare using numeric components")
	void concreteVersionsCompareUsingSemanticOrdering() {
		assertTrue(Version.isHigherThan(Version.V_1_21_10, Version.V_1_21_9));
		assertTrue(Version.isLowerThan(Version.V_26_1_1, Version.V_26_1_2));
		assertTrue(Version.V_26_1_2.isAtLeast(Version.V_26_1_1));
		assertFalse(Version.V_1_20_5.isAtLeast(Version.V_1_20_6));
	}

	private static void assertResolvesTo(String rawVersion, Version expected) {
		assertEquals(expected, Version.of(rawVersion));
	}
}
