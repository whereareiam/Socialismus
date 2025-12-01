package me.whereareiam.socialismus.input.container;

import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing chat message history storage.
 * Provides methods for adding, removing, and retrieving formatted chat messages
 * from the history container.
 */
public interface ChatHistoryContainerService {
    /**
     * Adds a formatted message to the history with the specified ID.
     * @param id unique identifier for the message
     * @param message the formatted chat message to store
     */
    void addMessage(int id, FormattedChatMessage message);

    /**
     * Removes a message with the specified ID from the history.
     * @param id the ID of the message to remove
     * @return true if the message was removed, false if not found
     */
    boolean removeMessage(int id);

    /**
     * Removes the specified amount of oldest messages from the history.
     * @param amount number of messages to remove
     * @return the actual number of messages removed
     */
    int removeMessages(int amount);

    /**
     * Retrieves a message by its ID.
     * @param id the ID of the message to retrieve
     * @return an Optional containing the message if found, empty otherwise
     */
    Optional<FormattedChatMessage> getMessage(int id);

    /**
     * Retrieves all messages sent by a specific player username.
     * @param username the player's username
     * @return list of messages sent by the player
     */
    List<FormattedChatMessage> getMessages(String username);

    /**
     * Retrieves all messages sent by a specific player UUID.
     * @param uniqueId the player's unique identifier
     * @return list of messages sent by the player
     */
    List<FormattedChatMessage> getMessages(UUID uniqueId);

    /**
     * Retrieves all messages from the history.
     * @return list of all stored messages
     */
    List<FormattedChatMessage> getMessages();
}