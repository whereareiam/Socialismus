package me.whereareiam.socialismus.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.event.base.Event;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;

/**
 * Event fired when a player is removed from the PlayerRegistry.
 * This typically occurs when a player disconnects from the server.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class SocialismusPlayerRemovedEvent implements Event {
    /**
     * The SocialismusPlayer instance being removed
     */
    private final SocialismusPlayer player;
}