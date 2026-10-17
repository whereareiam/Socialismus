package me.whereareiam.socialismus.testing.environment;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Every journey belongs to exactly one suite, so that running every suite runs every journey once. The suites
 * are the ones {@code socialismus-testing/build.gradle.kts} declares; {@code anvilTest} runs what no suite
 * claims, which is only this check.
 */
class JourneySuitesTest {
	private static final String PACKAGE = "me.whereareiam.socialismus.testing";

	@Test
	void placesEveryJourneyInExactlyOneSuite() {
		Set<String> suites = suites();
		assertFalse(suites.isEmpty(), "anvilTest names no suites");

		List<String> misplaced = journeys()
				.filter(journey -> suitesOf(journey, suites).size() != 1)
				.map(journey -> journey.getDeclaringClass().getName() + "#" + journey.getName() + " " + suitesOf(journey, suites))
				.toList();

		assertEquals(List.of(), misplaced, "journeys outside exactly one of the suites " + suites);
	}

	private static Set<String> suites() {
		String suites = System.getProperty("socialismus.journeys.suites", "");
		if (suites.isBlank()) return Set.of();

		return new TreeSet<>(List.of(suites.split(",")));
	}

	private static Stream<Method> journeys() {
		return ReflectionSupport.findAllClassesInPackage(PACKAGE, type -> type != JourneySuitesTest.class, name -> true).stream()
				.flatMap(type -> Arrays.stream(type.getDeclaredMethods()))
				.filter(method -> AnnotationSupport.isAnnotated(method, Testable.class));
	}

	private static List<String> suitesOf(Method journey, Set<String> suites) {
		return Stream.concat(tags(journey.getDeclaringClass()), tags(journey))
				.filter(suites::contains)
				.distinct()
				.sorted()
				.toList();
	}

	private static Stream<String> tags(AnnotatedElement element) {
		return AnnotationSupport.findRepeatableAnnotations(element, Tag.class).stream().map(Tag::value);
	}
}
