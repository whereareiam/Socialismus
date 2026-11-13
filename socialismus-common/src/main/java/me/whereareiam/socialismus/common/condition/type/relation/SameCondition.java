package me.whereareiam.socialismus.common.condition.type.relation;

import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.socialismus.common.condition.type.PlaceholderReader;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.type.PlatformType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * {@code same: world}, {@code same: server} or {@code same: { placeholder: "%townyadvanced_town%" }}: holds when a
 * player, such as a chat's recipient, shares a place with another, such as the sender. Two players this game server
 * hosts share its server; elsewhere the server is the one the proxy names. A value nobody knows, or an empty
 * placeholder value, is shared with nobody, so two players without a town are not in the same town. {@code world}
 * cannot be named on a proxy, which knows no worlds, and without a placeholder plugin the placeholder form fails
 * without a reason.
 */
@RequiredArgsConstructor
public final class SameCondition implements ConditionType.Pair<SocialismusPlayer, SocialismusPlayer, SameCondition.Settings> {
	private final Provider<PlayerRegistry> players;
	private final PlaceholderReader placeholders;

	@Override
	public @NotNull String name() {
		return "same";
	}

	@Override
	public @NotNull Class<SocialismusPlayer> first() {
		return SocialismusPlayer.class;
	}

	@Override
	public @NotNull Class<SocialismusPlayer> second() {
		return SocialismusPlayer.class;
	}

	@Override
	public @NotNull Class<Settings> settings() {
		return Settings.class;
	}

	@Override
	public @NotNull Optional<String> main() {
		return Optional.of("place");
	}

	@Override
	public @NotNull Set<Class<? extends Reason>> reasons() {
		return Set.of(Outside.class, Inside.class);
	}

	@Override
	public @NotNull Outcome holds(@NotNull SocialismusPlayer player, @NotNull SocialismusPlayer other, @NotNull Settings settings) {
		if (settings.placeholder() != null) return samePlaceholder(player, other, settings.placeholder());
		if (settings.where() == Place.WORLD) return compare("world", player.getLocation(), other.getLocation());
		if (PlatformType.isGameServer() && hosted(player) && hosted(other)) return Outcome.pass(new Inside("server"));

		return compare("server", player.getServer(), other.getServer());
	}

	private Outcome samePlaceholder(SocialismusPlayer player, SocialismusPlayer other, String text) {
		Optional<String> own = placeholders.read(player, text);
		Optional<String> theirs = placeholders.read(other, text);
		if (own.isEmpty() || theirs.isEmpty()) return Outcome.fail();

		String place = theirs.get().isBlank() ? text : theirs.get();

		return compare(place, own.get(), theirs.get());
	}

	private boolean hosted(SocialismusPlayer player) {
		return players.get().hasPlayerData(player.getUniqueId());
	}

	private static Outcome compare(String place, @Nullable String value, @Nullable String other) {
		boolean known = value != null && !value.isBlank();

		return known && value.equals(other)
				? Outcome.pass(new Inside(place))
				: Outcome.fail(new Outside(place));
	}

	/**
	 * The places two players can share by name, written in lower case.
	 */
	public enum Place {
		WORLD,
		SERVER;

		static @NotNull Place of(@NotNull String text) {
			for (Place place : values())
				if (place.name().equalsIgnoreCase(text)) return place;

			throw new IllegalArgumentException("same knows world and server, not " + text);
		}
	}

	/**
	 * The condition's settings: a place or a placeholder, exactly one of them.
	 *
	 * @param place       the place, {@code world} or {@code server}
	 * @param placeholder a text with placeholders whose value the players share, such as {@code %townyadvanced_town%}
	 */
	public record Settings(@Nullable String place, @Nullable String placeholder) {
		public Settings {
			if ((place == null) == (placeholder == null))
				throw new IllegalArgumentException("same needs either world, server or a placeholder");
			if (place != null && Place.of(place) == Place.WORLD && !PlatformType.isGameServer())
				throw new IllegalArgumentException("same: world does not work on a proxy, which knows no worlds");
		}

		/**
		 * The place named, read once from its text.
		 *
		 * @return the place, {@code null} for the placeholder form
		 */
		public @Nullable Place where() {
			return place == null ? null : Place.of(place);
		}
	}

	/**
	 * The player is not in the other's place: {@code same.outside}.
	 *
	 * @param place what they would share: {@code world}, {@code server}, or the other player's placeholder value
	 */
	public record Outside(@NotNull String place) implements Reason {
		@Override
		public @NotNull Map<String, String> values() {
			return Map.of("place", place);
		}
	}

	/**
	 * The player is in the other's place, told when a {@code not} around the condition fails: {@code same.inside}.
	 *
	 * @param place what they share: {@code world}, {@code server}, or the placeholder value
	 */
	public record Inside(@NotNull String place) implements Reason {
		@Override
		public @NotNull Map<String, String> values() {
			return Map.of("place", place);
		}
	}
}
