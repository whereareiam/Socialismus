package me.whereareiam.socialismus.model.chat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.model.config.message.RequirementReasons;
import me.whereareiam.socialismus.type.chat.Participants;

import java.util.Map;

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
     * Message shown when a trigger is used without any message content.
     */
    private String emptyMessage;

    /**
     * How chat words the requirements of chats it denies a player, by participant and then by the requirement's
     * key, as the chats' own {@code requirements} hold them. Its placeholders are the requirement's and
     * {@code {chat}}, the chat that denied the player; what it leaves unworded comes from {@code requirements} in
     * {@code messages.yml}.
     *
     * @see me.whereareiam.socialismus.service.requirement.RequirementReasonService
     */
    private Map<Participants, Map<String, RequirementReasons>> requirements;

    /**
     * Configuration for clearing chat messages.
     */
    private ClearFormat clearFormat;

    /**
     * The notices of turning a chat off, with {@code {chat}} filled in.
     */
    private Toggle toggle;

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

    /**
     * The notices of turning a chat off.
     */
    @Getter
    @Setter
    @ToString
    public static class Toggle {
        private String off;

        private String on;

        private String notToggleable;

        private String unknown;

        private String unavailable;
    }
}
