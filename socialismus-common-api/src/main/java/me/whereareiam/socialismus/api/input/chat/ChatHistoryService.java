package me.whereareiam.socialismus.api.input.chat;

/**
 * Service interface for managing chat history operations.
 * Provides methods for removing messages from chat history based on different criteria.
 * This service acts as a facade for chat history management operations.
 */
public interface ChatHistoryService {
    /**
     * Removes a specific message from the chat history.
     *
     * @param id the unique identifier of the message to remove
     * @return true if the message was successfully removed, false otherwise
     */
    boolean removeMessage(int id);

    /**
     * Removes a specified number of oldest messages from the chat history.
     *
     * @param amount the number of messages to remove
     * @return the actual number of messages removed
     */
    int removeMessages(int amount);

    /**
     * Removes all messages from a specific user in the chat history.
     *
     * @param username the username whose messages should be removed
     * @return the number of messages removed
     */
    int removeMessages(String username);
}