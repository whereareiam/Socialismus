package me.whereareiam.socialismus.api.model.player;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Represents a dummy player that can execute commands in the Socialismus plugin.
 * This class extends {@link DummyPlayer} to add command sender functionality,
 * allowing the dummy player to interact with the command system while maintaining
 * player-like properties.
 */
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
public class DummyCommandPlayer extends DummyPlayer {
    /**
     * The command sender object associated with this dummy player
     */
    private Object commandSender;

    /**
     * Creates a new DummyCommandPlayer instance from an existing DummyPlayer and a command sender.
     *
     * @param player the base dummy player to copy properties from
     * @param commandSender the command sender object to associate with the new instance
     * @return a new DummyCommandPlayer with properties from both sources
     */
    public static DummyCommandPlayer from(DummyPlayer player, Object commandSender) {
        return DummyCommandPlayer.builder()
                .commandSender(commandSender)
                .username(player.getUsername())
                .uniqueId(player.getUniqueId())
                .audience(player.getAudience())
                .location(player.getLocation())
                .locale(player.getLocale())
                .build();
    }
}