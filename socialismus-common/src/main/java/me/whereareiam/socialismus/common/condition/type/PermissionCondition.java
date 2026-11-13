package me.whereareiam.socialismus.common.condition.type;

import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@code permission: socialismus.chat.local}: holds when the player has every permission listed.
 */
public final class PermissionCondition implements ConditionType.Single<SocialismusPlayer, PermissionCondition.Settings> {
	@Override
	public @NotNull String name() {
		return "permission";
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
	public @NotNull Set<Class<? extends Reason>> reasons() {
		return Set.of(Missing.class, Held.class);
	}

	@Override
	public @NotNull Outcome holds(@NotNull SocialismusPlayer player, @NotNull Settings settings) {
		List<String> missing = settings.permissions().stream().filter(permission -> !player.hasPermission(permission)).toList();

		return missing.isEmpty() ? Outcome.pass(new Held(settings.permissions())) : Outcome.fail(new Missing(missing));
	}

	/**
	 * The condition's settings.
	 *
	 * @param permissions the permissions the player must have
	 */
	public record Settings(@NotNull List<String> permissions) {
	}

	/**
	 * The player lacks permissions: {@code permission.missing}.
	 *
	 * @param permissions the permissions the player lacks
	 */
	public record Missing(@NotNull List<String> permissions) implements Reason {
		@Override
		public @NotNull Map<String, String> values() {
			return Map.of("permissions", String.join(", ", permissions));
		}
	}

	/**
	 * The player has the permissions, told when a {@code not} around the condition fails: {@code permission.held}.
	 *
	 * @param permissions the permissions the player has
	 */
	public record Held(@NotNull List<String> permissions) implements Reason {
		@Override
		public @NotNull Map<String, String> values() {
			return Map.of("permissions", String.join(", ", permissions));
		}
	}
}
