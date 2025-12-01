package me.whereareiam.socialismus.input.container;

import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.InternalChat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service interface for managing chat containers in the Socialismus system.
 * Provides methods for adding, updating, retrieving, and checking existence of chats.
 */
public interface ChatContainerService {
	/**
	 * Adds an internal chat to the container.
	 *
	 * @param chat the internal chat to add
	 */
	void addChat(InternalChat chat);

	/**
	 * Adds a regular chat to the container.
	 *
	 * @param chat the chat to add
	 */
	void addChat(Chat chat);

	/**
	 * Updates an existing chat identified by its ID.
	 *
	 * @param id   the ID of the chat to updater
	 * @param chat the new chat data
	 */
	void updateChat(String id, Chat chat);

	/**
	 * Checks if a chat with the given ID exists.
	 *
	 * @param id the chat ID to check
	 * @return true if the chat exists, false otherwise
	 */
	boolean hasChat(String id);

	/**
	 * Checks if a chat with the given symbol exists.
	 *
	 * @param symbol the chat symbol to check
	 * @return true if the chat exists, false otherwise
	 */
	boolean hasChatBySymbol(String symbol);

	/**
	 * Retrieves an internal chat by its ID.
	 *
	 * @param id the ID of the chat to retrieve
	 * @return an Optional containing the chat if found, empty otherwise
	 */
	Optional<InternalChat> getChat(String id);

	/**
	 * Retrieves all internal chats matching the given symbol.
	 *
	 * @param symbol the symbol to match
	 * @return a list of matching internal chats
	 */
	List<InternalChat> getChatBySymbol(String symbol);

	/**
	 * Retrieves all internal chats in the container.
	 *
	 * @return a set of all internal chats
	 */
	Set<InternalChat> getChats();
}