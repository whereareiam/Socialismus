package me.whereareiam.socialismus.common.condition.type;

import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Whether where a player is, a world or a server, is one of those listed: the outcome {@code world} and
 * {@code server} share, with their reasons {@code outside} and {@code inside}.
 */
final class Location {
	private Location() {
	}

	static @NotNull Outcome among(@NotNull List<String> places, @NotNull String place, @NotNull String plural, @NotNull String singular) {
		return places.contains(place)
				? Outcome.pass(new Inside(singular, place))
				: Outcome.fail(new Outside(plural, singular, places, place));
	}

	/**
	 * The player is outside the places listed: {@code world.outside}, {@code server.outside}.
	 *
	 * @param plural   the placeholder of the places listed, such as {@code worlds}
	 * @param singular the placeholder of where the player is, such as {@code world}
	 * @param places   the places listed
	 * @param place    where the player is
	 */
	record Outside(String plural, String singular, List<String> places, String place) implements Reason {
		@Override
		public @NotNull Map<String, String> values() {
			return Map.of(plural, String.join(", ", places), singular, place);
		}
	}

	/**
	 * The player is in one of the places listed, told when a {@code not} around the condition fails:
	 * {@code world.inside}, {@code server.inside}.
	 *
	 * @param singular the placeholder of where the player is, such as {@code world}
	 * @param place    where the player is
	 */
	record Inside(String singular, String place) implements Reason {
		@Override
		public @NotNull Map<String, String> values() {
			return Map.of(singular, place);
		}
	}
}
