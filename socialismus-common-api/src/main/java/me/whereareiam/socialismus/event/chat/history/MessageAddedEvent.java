package me.whereareiam.socialismus.event.chat.history;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.event.base.CancellableEvent;
import me.whereareiam.socialismus.event.base.Event;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;

/**
 * Event that is triggered when a new message is being added to the chat history.
 * This event contains the formatted message being added and can be cancelled
 * to prevent the message from being stored in the history.
 *
 * <p>Implements both {@link Event} and {@link CancellableEvent} interfaces to
 * provide event handling and cancellation functionality.</p>
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class MessageAddedEvent implements Event, CancellableEvent {
    /**
     * The formatted chat message being added to the history
     */
    private final FormattedChatMessage chatMessage;

    /**
     * The cancelled state of the event
     */
    private boolean cancelled;
}