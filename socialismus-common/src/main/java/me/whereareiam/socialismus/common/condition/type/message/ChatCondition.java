package me.whereareiam.socialismus.common.condition.type.message;

import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@code chat: [local, global]}: holds when the message is in one of the chats listed. A message whose chat is not
 * chosen yet, as at a chat's own {@code requires}, is in no chat.
 */
public final class ChatCondition implements ConditionType.Single<ChatMessage, ChatCondition.Settings> {
	@Override
	public @NotNull String name() {
		return "chat";
	}

	@Override
	public @NotNull Class<ChatMessage> subject() {
		return ChatMessage.class;
	}

	@Override
	public @NotNull Class<Settings> settings() {
		return Settings.class;
	}

	@Override
	public @NotNull Set<Class<? extends Reason>> reasons() {
		return Set.of(Outside.class, Inside.class);
	}

	@Override
	public @NotNull Outcome holds(@NotNull ChatMessage message, @NotNull Settings settings) {
		Chat chat = message.getChat();
		if (chat == null) return Outcome.fail();

		return settings.chats().contains(chat.getId())
				? Outcome.pass(new Inside(chat.getId()))
				: Outcome.fail(new Outside(settings.chats(), chat.getId()));
	}

	/**
	 * The condition's settings.
	 *
	 * @param chats the ids of the chats the message may be in
	 */
	public record Settings(@NotNull List<String> chats) {
	}

	/**
	 * The message is in another chat: {@code chat.outside}.
	 *
	 * @param chats the chats listed
	 * @param chat  the message's chat
	 */
	public record Outside(@NotNull List<String> chats, @NotNull String chat) implements Reason {
		@Override
		public @NotNull Map<String, String> values() {
			return Map.of("chats", String.join(", ", chats), "chat", chat);
		}
	}

	/**
	 * The message is in one of the chats listed, told when a {@code not} around the condition fails:
	 * {@code chat.inside}.
	 *
	 * @param chat the message's chat
	 */
	public record Inside(@NotNull String chat) implements Reason {
		@Override
		public @NotNull Map<String, String> values() {
			return Map.of("chat", chat);
		}
	}
}
