package me.whereareiam.socialismus.service.chat;

import me.whereareiam.socialismus.model.chat.Chat;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * The conditions of the chats, bound for each chat once their condition types are registered and handed out as the
 * chat's {@link ChatPlaces}, which check them at once, on the thread that asks, such as a server's chat thread:
 *
 * <pre>{@code
 * chatConditions.of(chat).ifPresent(places -> {
 *     if (places.writes(message) instanceof Verdict.Failed failed) refuse(message.getSender(), failed);
 * });
 * }</pre>
 *
 * <p>A chat's places follow it until the chat is replaced or the chats are loaded again, so a caller asks for them
 * where it checks them, not once for good.</p>
 */
public interface ChatConditions {
	/**
	 * The scope of the phrases chats tell, such as {@code chat.denied.world.outside} or {@code chat.staff-only}.
	 */
	String PHRASE_SCOPE = "chat";

	/**
	 * Finds the places of a chat.
	 *
	 * @param chat the chat
	 * @return its places; empty when it has none, which happens before Socialismus has started, while the chats are
	 * loaded again, or for a chat Socialismus does not hold, such as one whose lists cannot be bound
	 */
	@NotNull Optional<ChatPlaces> of(@NotNull Chat chat);
}
