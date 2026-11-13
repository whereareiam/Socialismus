package me.whereareiam.socialismus.common.condition.type.relation;

import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * {@code nearby: 200}: holds when a player, such as a chat's recipient, is in the same world as another, such as the
 * sender, and at most the blocks given away. Registered on game servers only, since a proxy knows no positions; a
 * player of another server is never nearby.
 */
public final class NearbyCondition implements ConditionType.Pair<SocialismusPlayer, SocialismusPlayer, NearbyCondition.Settings> {
	@Override
	public @NotNull String name() {
		return "nearby";
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
		return Optional.of("blocks");
	}

	@Override
	public @NotNull Set<Class<? extends Reason>> reasons() {
		return Set.of(Outside.class, Inside.class);
	}

	@Override
	public @NotNull Outcome holds(@NotNull SocialismusPlayer player, @NotNull SocialismusPlayer other, @NotNull Settings settings) {
		return player.isWithinRange(other, settings.blocks())
				? Outcome.pass(new Inside(settings.blocks()))
				: Outcome.fail(new Outside(settings.blocks()));
	}

	/**
	 * The condition's settings.
	 *
	 * @param blocks the distance in blocks the players may be apart
	 */
	public record Settings(int blocks) {
		public Settings {
			if (blocks <= 0) throw new IllegalArgumentException("nearby needs a number of blocks above 0, not " + blocks);
		}
	}

	/**
	 * The player is farther away or in another world: {@code nearby.outside}.
	 *
	 * @param blocks the distance given
	 */
	public record Outside(int blocks) implements Reason {
		@Override
		public @NotNull Map<String, String> values() {
			return Map.of("blocks", String.valueOf(blocks));
		}
	}

	/**
	 * The player is within the distance, told when a {@code not} around the condition fails: {@code nearby.inside}.
	 *
	 * @param blocks the distance given
	 */
	public record Inside(int blocks) implements Reason {
		@Override
		public @NotNull Map<String, String> values() {
			return Map.of("blocks", String.valueOf(blocks));
		}
	}
}
