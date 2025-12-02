package me.whereareiam.socialismus.event.chat.history;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.event.base.CancellableEvent;
import me.whereareiam.socialismus.event.base.Event;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;

/**
 * Event that is triggered when a message is being removed from the chat history.
 * This event contains the formatted message being removed and can be cancelled
 * to prevent the message from being deleted from the history.
 *
 * <p>Implements both {@link Event} and {@link CancellableEvent} interfaces to
 * provide event handling and cancellation functionality.</p>
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class MessageRemovedEvent implements Event, CancellableEvent {
    /**
     * The formatted chat message being removed from the history
     */
    private final FormattedChatMessage chatMessage;

    /**
     * The cancelled state of the event
     */
    private boolean cancelled;
}