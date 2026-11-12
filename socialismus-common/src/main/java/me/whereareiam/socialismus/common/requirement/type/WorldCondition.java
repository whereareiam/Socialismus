package me.whereareiam.socialismus.common.requirement.type;

import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * {@code world: [world, world_nether]}: holds when the player is in one of the worlds listed. Registered on game
 * servers only, since a proxy knows no worlds; a proxy's config that names it stops startup.
 */
public final class WorldCondition implements ConditionType.Single<SocialismusPlayer, WorldCondition.Settings> {
	@Override
	public @NotNull String name() {
		return "world";
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
		return Set.of(Location.Outside.class, Location.Inside.class);
	}

	@Override
	public @NotNull Outcome holds(@NotNull SocialismusPlayer player, @NotNull Settings settings) {
		return Location.among(settings.worlds(), String.valueOf(player.getLocation()), "worlds", "world");
	}

	/**
	 * The condition's settings.
	 *
	 * @param worlds the worlds the player may be in
	 */
	public record Settings(@NotNull List<String> worlds) {
	}
}
