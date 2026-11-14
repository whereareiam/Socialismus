package me.whereareiam.socialismus.module.mentionator.common.condition;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.model.NoSettings;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.service.chat.ChatConditionService;
import org.jetbrains.annotations.NotNull;

/**
 * {@code mentionator:reached: [target, message]}: holds when the message's chat reaches the player, as its
 * {@code reaches} decides, so {@code @here} tags only the players who read it.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class ReachedCondition implements ConditionType.Pair<SocialismusPlayer, ChatMessage, NoSettings> {
	private final ChatConditionService chatConditions;

	@Override
	public @NotNull String name() {
		return "reached";
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
		if (message.getChat() == null) return Outcome.fail();

		return Outcome.when(chatConditions.reaches(message.getChat(), player, message).passed());
	}
}
