package me.whereareiam.socialismus.module.bubbler.common.condition;

import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * {@code bubbler:chat: [fallback, local]}: holds when a message of one of the chats listed raised the bubble; a
 * bubble another activator raised is in no chat.
 */
public final class ChatActivationCondition implements ConditionType.Single<BubbleMessage, ChatActivationCondition.Settings> {
	@Override
	public @NotNull String name() {
		return "chat";
	}

	@Override
	public @NotNull Class<BubbleMessage> subject() {
		return BubbleMessage.class;
	}

	@Override
	public @NotNull Class<Settings> settings() {
		return Settings.class;
	}

	@Override
	public @NotNull Outcome holds(@NotNull BubbleMessage message, @NotNull Settings settings) {
		return Outcome.when(message.getChat() != null && settings.chats().contains(message.getChat()));
	}

	/**
	 * The condition's settings.
	 *
	 * @param chats the ids of the chats whose messages may raise the bubble
	 */
	public record Settings(@NotNull List<String> chats) {
	}
}
