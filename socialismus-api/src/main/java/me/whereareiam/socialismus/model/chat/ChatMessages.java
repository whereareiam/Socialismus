package me.whereareiam.socialismus.model.chat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
     * How a chat's refusal of its sender is told, by the reason's key, such as {@code world.outside}. The texts take
     * the reason's placeholders and {@code {chat}}, the chat that refused the player; what they leave unworded comes
     * from {@code requirements} in {@code messages.yml}.
     *
     * @see me.whereareiam.socialismus.service.requirement.RequirementReasonService
     */
    private Map<String, String> requirements = Map.of();

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
