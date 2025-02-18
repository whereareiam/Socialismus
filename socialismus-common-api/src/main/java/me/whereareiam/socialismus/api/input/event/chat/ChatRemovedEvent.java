package me.whereareiam.socialismus.api.input.event.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.api.input.event.base.CancellableEvent;
import me.whereareiam.socialismus.api.input.event.base.Event;
import me.whereareiam.socialismus.api.model.chat.Chat;

/**
 * Event that is triggered when a chat channel is being removed from the system.
 * This event can be cancelled to prevent the chat channel from being removed.
 *
 * <p>Implements both {@link Event} and {@link CancellableEvent} interfaces to
 * provide event handling and cancellation functionality.</p>
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class ChatRemovedEvent implements Event, CancellableEvent {
    /**
     * The chat channel that is being removed.
     */
    private final Chat chat;

    /**
     * The cancelled state of the event.
     */
    private boolean cancelled;
}