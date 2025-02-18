package me.whereareiam.socialismus.api.model.chat;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Configuration model for chat-related messages and formats.
 * Contains various message templates used throughout the chat system
 * for different scenarios and error states.
 *
 * <p>This singleton class is used to store message templates that are
 * loaded from the plugin's configuration.</p>
 */
@Getter
@Setter
@ToString
@Singleton
public class ChatMessages {
    /**
     * Message shown when no players are online.
     */
    private String noPlayers;

    /**
     * Message shown when no matching chat channel is found.
     */
    private String noChatMatch;

    /**
     * Message shown when no matching format is found.
     */
    private String noFormatMatch;

    /**
     * Message shown when no fallback chat channel is available.
     */
    private String noFallbackChat;

    /**
     * Message shown when no players are nearby.
     */
    private String noNearbyPlayers;

    /**
     * Configuration for clearing chat messages.
     */
    private ClearFormat clearFormat;

    /**
     * Nested configuration class for chat clearing format.
     */
    @Getter
    @Setter
    @ToString
    public static class ClearFormat {
        /**
         * The format string used when clearing chat.
         */
        private String format;
    }
}