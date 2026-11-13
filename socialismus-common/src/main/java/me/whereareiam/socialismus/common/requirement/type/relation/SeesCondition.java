package me.whereareiam.socialismus.common.requirement.type.relation;

import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.model.NoSettings;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * {@code sees:}: holds when a player, such as a chat's recipient, may see another, such as the sender, so that a
 * player a vanish plugin hides from them stays hidden. Where a platform does not know who is hidden from whom,
 * everyone sees everyone.
 */
public final class SeesCondition implements ConditionType.Pair<SocialismusPlayer, SocialismusPlayer, NoSettings> {
	@Override
	public @NotNull String name() {
		return "sees";
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
	public @NotNull Class<NoSettings> settings() {
		return NoSettings.class;
	}

	@Override
	public @NotNull Set<Class<? extends Reason>> reasons() {
		return Set.of(Hidden.class, Visible.class);
	}

	@Override
	public @NotNull Outcome holds(@NotNull SocialismusPlayer player, @NotNull SocialismusPlayer other, @NotNull NoSettings settings) {
		return player.canSee(other) ? Outcome.pass(new Visible()) : Outcome.fail(new Hidden());
	}

	/**
	 * The other player is hidden from the player: {@code sees.hidden}.
	 */
	public record Hidden() implements Reason {
	}

	/**
	 * The player sees the other, told when a {@code not} around the condition fails: {@code sees.visible}.
	 */
	public record Visible() implements Reason {
	}
}
