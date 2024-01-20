package me.whereareiam.socialismus.api.module;

import me.whereareiam.socialismus.api.model.chat.Chat;

import java.util.List;

/**
 * Chat manager
 *
 * @since 1.2.0
 */
public interface ChatModule extends Module {
	/**
	 * Allows to register a specific chat
	 *
	 * @param chat Chat
	 * @since 1.2.0
	 */
	void registerChat(Chat chat);

	/**
	 * Allows to unregister all chats
	 *
	 * @since 1.2.0
	 */
	void unregisterChats();

	/**
	 * Allows to get chat by id
	 *
	 * @param symbol Chat symbol
	 * @return Chat
	 * @since 1.2.0
	 */
	Chat getChatBySymbol(String symbol);

	/**
	 * Allows to get chat by command
	 *
	 * @param command Command
	 * @return Chat
	 * @since 1.2.0
	 */
	Chat getChatByCommand(String command);

	/**
	 * Allows to get chats
	 *
	 * @return List of chats
	 * @since 1.2.0
	 */
	List<Chat> getChats();

	/**
	 * Allows to set chats that will be used by the module
	 *
	 * @param chats List of chats
	 * @since 1.2.0
	 */
	void setChats(List<Chat> chats);
}
