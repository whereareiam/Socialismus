package me.whereareiam.socialismus.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.event.base.CancellableEvent;
import me.whereareiam.socialismus.event.base.Event;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;

/**
 * Event that is triggered when a SocialismusPlayer's properties are updated in the Socialismus plugin.
 * This event contains both the updated player instance and the previous state.
 * Implements both {@link Event} and {@link CancellableEvent} interfaces to provide
 * standard event functionality with cancellation support.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@SuppressWarnings("unused")
public class SocialismusPlayerUpdatedEvent implements Event, CancellableEvent {
    /**
     * The updated SocialismusPlayer instance with new properties
     */
    private final SocialismusPlayer player;

    /**
     * The previous state of the SocialismusPlayer before updates
     */
    private final SocialismusPlayer oldSocialismusPlayer;

    /**
     * The cancelled state of the event
     */
    private boolean cancelled;
}