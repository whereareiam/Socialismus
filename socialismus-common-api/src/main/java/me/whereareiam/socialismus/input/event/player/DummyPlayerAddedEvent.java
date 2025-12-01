package me.whereareiam.socialismus.input.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.input.event.base.CancellableEvent;
import me.whereareiam.socialismus.input.event.base.Event;
import me.whereareiam.socialismus.model.player.DummyPlayer;

/**
 * Event that is triggered when a new DummyPlayer is added to the Socialismus plugin.
 * This event can be cancelled to prevent the player from being added.
 * Implements both {@link Event} and {@link CancellableEvent} interfaces to provide
 * standard event functionality with cancellation support.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class DummyPlayerAddedEvent implements Event, CancellableEvent {
    /**
     * The DummyPlayer instance being added
     */
    private final DummyPlayer dummyPlayer;

    /**
     * The cancelled state of the event
     */
    private boolean cancelled;
}