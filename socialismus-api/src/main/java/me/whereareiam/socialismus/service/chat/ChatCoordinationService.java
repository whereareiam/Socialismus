package me.whereareiam.socialismus.service.chat;

import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import org.jetbrains.annotations.NotNull;

/**
 * The entry point of chat: a message a player writes on this server goes through {@link #coordinate}, and a message
 * written elsewhere, such as on another server of a network, through {@link #relay}.
 *
 * <p>A message written here is offered to modules as a
 * {@link me.whereareiam.socialismus.event.chat.ChatReceivedEvent}, its chat is selected and it is formatted; then
 * Socialismus delivers it to its recipients on this server and keeps it in the chat history. A relayed message is
 * only delivered and kept: it is neither offered to modules nor selected again, and a module that sends messages on
 * leaves it alone, since it is {@linkplain ChatMessage#isRelayed() relayed}.</p>
 */
public interface ChatCoordinationService {
	/**
	 * Takes a message a player wrote on this server through the whole of chat.
	 *
	 * @param chatMessage the message as the player wrote it
	 * @return the formatted message, cancelled when a module or the chat's rules stopped it
	 */
	FormattedChatMessage coordinate(ChatMessage chatMessage);

	/**
	 * Delivers a message written elsewhere to its recipients on this server, in the format it was given where it was
	 * written.
	 *
	 * @param message the formatted message; Socialismus marks it relayed and chooses its recipients here
	 */
	void relay(@NotNull FormattedChatMessage message);

	/**
	 * Delivers a message written elsewhere to its recipients on this server, formatted here by the formats of its
	 * chat.
	 *
	 * @param message the message with its chat; Socialismus marks it relayed, formats it and chooses its recipients
	 *                here
	 * @return the formatted message, cancelled when it could not be formatted or delivered
	 */
	FormattedChatMessage relay(@NotNull ChatMessage message);
}
