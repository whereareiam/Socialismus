package me.whereareiam.socialismus.module.dialogue.common.condition;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.model.NoSettings;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.IgnoreStore;
import org.jetbrains.annotations.NotNull;

/**
 * {@code dialogue:ignores: [target, sender]}: holds when a player ignores another through {@code /ignore}, such as a
 * tagged player who ignores the player tagging them. A player who meets {@code control.ignore.exempt} is ignored by
 * nobody.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class IgnoresCondition implements ConditionType.Pair<SocialismusPlayer, SocialismusPlayer, NoSettings> {
	private final IgnoreStore ignores;
	private final DialogueConditions conditions;

	@Override
	public @NotNull String name() {
		return "ignores";
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
	public @NotNull Outcome holds(@NotNull SocialismusPlayer player, @NotNull SocialismusPlayer other, @NotNull NoSettings settings) {
		if (conditions.exemptFromIgnore(other)) return Outcome.fail();

		return Outcome.when(ignores.ignores(player.getUniqueId(), other.getUniqueId()));
	}
}
