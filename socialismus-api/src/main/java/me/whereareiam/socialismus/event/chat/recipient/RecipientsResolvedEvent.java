package me.whereareiam.socialismus.event.chat.recipient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.event.base.CancellableEvent;
import me.whereareiam.socialismus.event.base.Event;
import me.whereareiam.socialismus.event.base.SynchronousEvent;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;

/**
 * Event that is fired after the recipients for a chat message have been resolved.
 * This event provides an opportunity to modify or cancel the message delivery process
 * after the final list of recipients has been determined.
 *
 * <p>Being a {@link CancellableEvent}, the message delivery can be cancelled by
 * setting the cancelled flag to true.</p>
 *
 * <p>Its listeners run on the thread that resolves the recipients, before the message is sent on, so a listener's
 * cancelling reaches the message.</p>
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
public class RecipientsResolvedEvent implements Event, CancellableEvent, SynchronousEvent {
    /**
     * The chat message with its resolved recipients
     */
    private final ChatMessage chatMessage;

    /**
     * Flag indicating whether the message delivery has been cancelled
     */
    private boolean cancelled;
}