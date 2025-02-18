package me.whereareiam.socialismus.api.input.event.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.api.input.event.base.CancellableEvent;
import me.whereareiam.socialismus.api.input.event.base.Event;
import me.whereareiam.socialismus.api.model.chat.message.FormattedChatMessage;

/**
 * Event triggered when a chat message is about to be broadcasted.
 * This event can be cancelled to prevent the message from being sent.
 *
 * <p>This event provides access to the formatted chat message and implements both
 * {@link Event} and {@link CancellableEvent} interfaces.</p>
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class ChatBroadcastEvent implements Event, CancellableEvent {
    /** The formatted chat message to be broadcasted */
    private final FormattedChatMessage chatMessage;

    /** Flag indicating if the event is cancelled */
    private boolean cancelled;
}
