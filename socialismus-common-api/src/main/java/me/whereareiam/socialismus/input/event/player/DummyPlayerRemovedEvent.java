package me.whereareiam.socialismus.input.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.input.event.base.CancellableEvent;
import me.whereareiam.socialismus.input.event.base.Event;
import me.whereareiam.socialismus.model.player.DummyPlayer;

/**
 * Event that is triggered when a DummyPlayer is being removed from the Socialismus plugin.
 * This event can be cancelled to prevent the player from being removed.
 * Implements both {@link Event} and {@link CancellableEvent} interfaces to provide
 * standard event functionality with cancellation support.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class DummyPlayerRemovedEvent implements Event, CancellableEvent {
    /**
     * The DummyPlayer instance being removed
     */
    private final DummyPlayer dummyPlayer;

    /**
     * The cancelled state of the event
     */
    private boolean cancelled;
}