package me.whereareiam.socialismus.api.input.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.api.input.event.base.CancellableEvent;
import me.whereareiam.socialismus.api.input.event.base.Event;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;

/**
 * Event that is triggered when a DummyPlayer's properties are updated in the Socialismus plugin.
 * This event contains both the updated player instance and the previous state.
 * Implements both {@link Event} and {@link CancellableEvent} interfaces to provide
 * standard event functionality with cancellation support.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class DummyPlayerUpdatedEvent implements Event, CancellableEvent {
    /**
     * The updated DummyPlayer instance with new properties
     */
    private final DummyPlayer dummyPlayer;

    /**
     * The previous state of the DummyPlayer before updates
     */
    private final DummyPlayer oldDummyPlayer;

    /**
     * The cancelled state of the event
     */
    private boolean cancelled;
}