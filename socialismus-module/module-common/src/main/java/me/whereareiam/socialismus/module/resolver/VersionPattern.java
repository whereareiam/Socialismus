package me.whereareiam.socialismus.module.resolver;

import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * The versions a {@code module.json} accepts, written as a regular expression the whole version must match.
 */
final class VersionPattern {
	private VersionPattern() {
	}

	/**
	 * Returns whether a version is accepted.
	 *
	 * @param pattern the accepted versions; any version when {@code null} or blank
	 * @param version the version to check
	 * @return whether the version matches the pattern; {@code false} for a pattern that is no regular expression
	 */
	static boolean accepts(@Nullable String pattern, @Nullable String version) {
		if (pattern == null || pattern.isBlank()) return true;
		if (version == null) return false;

		try {
			return Pattern.compile(pattern).matcher(version).matches();
		} catch (PatternSyntaxException exception) {
			return false;
		}
	}
}
