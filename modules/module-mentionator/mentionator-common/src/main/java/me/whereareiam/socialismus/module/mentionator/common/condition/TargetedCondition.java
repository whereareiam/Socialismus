package me.whereareiam.socialismus.module.mentionator.common.condition;

import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.model.NoSettings;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.mentionator.api.model.tagging.Tagging;
import org.jetbrains.annotations.NotNull;

/**
 * {@code mentionator:targeted: [recipient, message]}: holds when a tag of the message tags the player. In a chat's
 * {@code reaches} it lets tagged players receive the message, as in
 * {@code any: [nearby: 200, mentionator:targeted: [recipient, message]]}; in a tag's format it holds for the players
 * that tag tags.
 */
public final class TargetedCondition implements ConditionType.Pair<SocialismusPlayer, ChatMessage, NoSettings> {
	@Override
	public @NotNull String name() {
		return "targeted";
	}

	@Override
	public @NotNull Class<SocialismusPlayer> first() {
		return SocialismusPlayer.class;
	}

	@Override
	public @NotNull Class<ChatMessage> second() {
		return ChatMessage.class;
	}

	@Override
	public @NotNull Class<NoSettings> settings() {
		return NoSettings.class;
	}

	@Override
	public @NotNull Outcome holds(@NotNull SocialismusPlayer player, @NotNull ChatMessage message, @NotNull NoSettings settings) {
		return Outcome.when(message.getAttribute(Tagging.ATTRIBUTE)
				.map(tagging -> tagging.targets(player.getUniqueId()))
				.orElse(false));
	}
}
