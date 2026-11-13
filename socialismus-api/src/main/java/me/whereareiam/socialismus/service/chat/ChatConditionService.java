package me.whereareiam.socialismus.service.chat;

import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatFormat;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.service.style.TextStyle;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * The conditions of the chats and of {@code chat/settings.yml}, bound once their condition types are registered
 * and checked at once, on the thread that asks, such as a server's chat thread. A chat that is not bound, which
 * happens only before Socialismus has started, meets nobody.
 *
 * <pre>{@code
 * Verdict verdict = chatConditions.writes(chat, message);
 * if (verdict instanceof Verdict.Failed failed) refuse(message.getSender(), failed);
 * }</pre>
 */
public interface ChatConditionService {
	/**
	 * The scope of the phrases chats tell, such as {@code chat.denied.world.outside} or {@code chat.staff-only}.
	 */
	String PHRASE_SCOPE = "chat";

	/**
	 * Checks whether the sender of a message may write in a chat: the chat's {@code requires}.
	 *
	 * @param chat    the chat
	 * @param message the message, with its sender
	 * @return the verdict; a failed one names the condition and its reason
	 */
	@NotNull Verdict writes(@NotNull Chat chat, @NotNull ChatMessage message);

	/**
	 * Checks whether a message of a chat reaches a player: the chat's {@code reaches}.
	 *
	 * @param chat      the chat
	 * @param recipient the player
	 * @param message   the message, with its sender
	 * @return the verdict; a failed one names the condition and its reason, which the sender is told when the
	 * message reaches nobody
	 */
	@NotNull Verdict reaches(@NotNull Chat chat, @NotNull SocialismusPlayer recipient, @NotNull ChatMessage message);

	/**
	 * Chooses the format of a message: the last of the chat's formats whose {@code requires} the sender meets.
	 *
	 * @param chat    the chat
	 * @param message the message, with its sender
	 * @return the format, empty when the sender meets none
	 */
	@NotNull Optional<ChatFormat> format(@NotNull Chat chat, @NotNull ChatMessage message);

	/**
	 * Chooses the style a sender writes with in a chat: the last of the chat's {@code parameters.styles} whose
	 * conditions they meet.
	 *
	 * @param chat   the chat
	 * @param sender the sender
	 * @return the style, {@link TextStyle#PLAIN} when they meet none or the chat accepts none
	 */
	@NotNull TextStyle style(@NotNull Chat chat, @NotNull SocialismusPlayer sender);

	/**
	 * Checks whether a player may clear messages from chat history: {@code history.requires}.
	 *
	 * @param viewer the player who sees the messages
	 * @return whether the player sees the clear button
	 */
	boolean clears(@NotNull SocialismusPlayer viewer);

	/**
	 * Checks whether nobody clears a sender's messages: {@code history.bypass}.
	 *
	 * @param sender the sender
	 * @return whether the sender's messages stay
	 */
	boolean keepsHistory(@NotNull SocialismusPlayer sender);

	/**
	 * Checks whether a sender's messages reach players who turned the chat off: {@code toggle.bypass}.
	 *
	 * @param sender the sender
	 * @return whether the sender's messages reach them
	 */
	boolean bypassesToggle(@NotNull SocialismusPlayer sender);
}
