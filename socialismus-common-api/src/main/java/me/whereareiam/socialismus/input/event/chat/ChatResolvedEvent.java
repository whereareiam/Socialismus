package me.whereareiam.socialismus.input.event.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.input.event.base.CancellableEvent;
import me.whereareiam.socialismus.input.event.base.Event;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;

/**
 * Event that is triggered when a chat message has been resolved to a specific chat channel.
 * This event occurs after the chat channel for a message has been determined but before
 * the message is actually processed. The event can be cancelled to prevent further processing.
 *
 * <p>Implements both {@link Event} and {@link CancellableEvent} interfaces to
 * provide event handling and cancellation functionality.</p>
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
public class ChatResolvedEvent implements Event, CancellableEvent {
    /**
     * The chat message that has been resolved.
     */
    private final ChatMessage chatMessage;

    /**
     * The resolved chat channel for the message.
     */
    private Chat chat;

    /**
     * The cancelled state of the event.
     */
    private boolean cancelled;
}