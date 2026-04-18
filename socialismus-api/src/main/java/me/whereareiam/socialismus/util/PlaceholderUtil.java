package me.whereareiam.socialismus.util;

import me.whereareiam.socialismus.integration.placeholder.PlaceholderIntegration;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class PlaceholderUtil {
	public static String resolve(
			@NotNull PlaceholderIntegration integration,
			@NotNull UUID uniqueId,
			@NotNull String text
	) {
		return switch (integration.resolutionMode()) {
			case CHAINED -> resolveChained(integration, uniqueId, text);
			case SINGLE_PASS -> integration.resolve(uniqueId, text);
		};
	}

	public static String resolveChained(
			@NotNull PlaceholderIntegration integration,
			@NotNull UUID uniqueId,
			@NotNull String text
	) {
		String resolved = text;
		Set<String> seenValues = new HashSet<>();

		while (seenValues.add(resolved) && containsPlaceholderSyntax(resolved)) {
			String previous = resolved;
			resolved = integration.resolve(uniqueId, previous);

			if (resolved.equals(previous))
				break;
		}

		return resolved;
	}

	private static boolean containsPlaceholderSyntax(String text) {
		return text.indexOf('%') >= 0;
	}
}
