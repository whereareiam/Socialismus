package me.whereareiam.socialismus.common.condition.type.recording;

import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.model.Line;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * {@code cooldown: 30s}, or {@code cooldown: { duration: 5m, key: everyone }}: holds unless the player did what
 * the line allows within that time. A use counts once it happened, such as a chat message that went out, not when it
 * was checked; the line's path keeps cooldowns apart, and a {@code key} shares one between lines. With
 * SocialismusNetwork, every server of the network knows a player's cooldowns.
 */
@RequiredArgsConstructor
public final class CooldownCondition implements ConditionType.Recording<SocialismusPlayer, CooldownCondition.Settings> {
	private final Cooldowns cooldowns;

	@Override
	public @NotNull String name() {
		return "cooldown";
	}

	@Override
	public @NotNull Class<SocialismusPlayer> subject() {
		return SocialismusPlayer.class;
	}

	@Override
	public @NotNull Class<Settings> settings() {
		return Settings.class;
	}

	@Override
	public @NotNull Optional<String> main() {
		return Optional.of("duration");
	}

	@Override
	public @NotNull Set<Class<? extends Reason>> reasons() {
		return Set.of(Waiting.class);
	}

	@Override
	public @NotNull Outcome holds(@NotNull SocialismusPlayer player, @NotNull Settings settings, @NotNull Line line) {
		return cooldowns.remaining(player.getUniqueId(), key(settings, line))
				.map(remaining -> Outcome.fail(new Waiting(remaining)))
				.orElseGet(Outcome::pass);
	}

	@Override
	public void record(@NotNull SocialismusPlayer player, @NotNull Settings settings, @NotNull Line line) {
		cooldowns.start(player.getUniqueId(), key(settings, line), settings.duration());
	}

	private static String key(Settings settings, Line line) {
		return settings.key() == null ? line.getPath() : "key:" + settings.key();
	}

	/**
	 * The condition's settings.
	 *
	 * @param duration how long a use keeps the line from holding again
	 * @param key      a name lines share one cooldown by; {@code null} for the line's own
	 */
	public record Settings(@NotNull Duration duration, @Nullable String key) {
		public Settings {
			if (duration.isNegative() || duration.isZero())
				throw new IllegalArgumentException("a cooldown lasts a positive time, not " + duration);
		}
	}

	/**
	 * The player has to wait: {@code cooldown.waiting}.
	 *
	 * @param remaining how long
	 */
	public record Waiting(@NotNull Duration remaining) implements Reason {
		@Override
		public @NotNull Map<String, String> values() {
			return Map.of("remaining", spoken(remaining));
		}

		// 1h 5m, 2m 30s, 12s: the largest units, with seconds rounded up so a player never reads 0s.
		private static String spoken(Duration remaining) {
			long seconds = Math.max(1, (remaining.toMillis() + 999) / 1000);
			long hours = seconds / 3600;
			long minutes = seconds % 3600 / 60;
			long rest = seconds % 60;
			if (hours > 0) return minutes > 0 ? hours + "h " + minutes + "m" : hours + "h";
			if (minutes > 0) return rest > 0 ? minutes + "m " + rest + "s" : minutes + "m";

			return rest + "s";
		}
	}
}
