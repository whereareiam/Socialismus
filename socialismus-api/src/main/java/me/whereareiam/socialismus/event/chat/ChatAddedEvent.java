package me.whereareiam.socialismus.event.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.event.base.CancellableEvent;
import me.whereareiam.socialismus.event.base.Event;
import me.whereareiam.socialismus.model.chat.Chat;

/**
 * Event triggered when a new chat is added to the system.
 * This event can be cancelled to prevent the chat from being added.
 *
 * <p>This event provides access to the chat being added and implements both
 * {@link Event} and {@link CancellableEvent} interfaces.</p>
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class ChatAddedEvent implements Event, CancellableEvent {
    /** The chat being added */
    private final Chat chat;

    /** Flag indicating if the event is cancelled */
    private boolean cancelled;
}
