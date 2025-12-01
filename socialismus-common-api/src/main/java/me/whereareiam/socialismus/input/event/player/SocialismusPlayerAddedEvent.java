package me.whereareiam.socialismus.input.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.input.event.base.SynchronousEvent;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;

/**
 * Event fired when a player is added to the PlayerRegistry.
 * This occurs when a player's data is first synced to the registry.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class SocialismusPlayerAddedEvent implements SynchronousEvent {
    /**
     * The SocialismusPlayer instance being added
     */
    private final SocialismusPlayer player;
}