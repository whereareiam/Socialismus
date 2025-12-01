package me.whereareiam.socialismus.input.event.chat.recipient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.input.event.base.CancellableEvent;
import me.whereareiam.socialismus.input.event.base.Event;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;

import java.util.Collection;

/**
 * Event that is fired when a new set of recipients is selected for a chat message.
 * This event allows modification or cancellation of the recipient selection process
 * before the recipients are finalized.
 *
 * <p>The event provides access to both the chat message and the newly selected
 * set of recipients, allowing for custom filtering or modification of the recipient list.</p>
 *
 * <p>Being a {@link CancellableEvent}, the recipient selection can be cancelled by
 * setting the cancelled flag to true.</p>
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
public class RecipientsSelectedEvent implements Event, CancellableEvent {
    /**
     * The chat message for which recipients are being selected
     */
    private final ChatMessage chatMessage;

    /**
     * The newly selected set of recipients for the message
     */
    private final Collection<SocialismusPlayer> newRecipients;

    /**
     * Flag indicating whether the recipient selection has been cancelled
     */
    private boolean cancelled;
}