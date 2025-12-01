package me.whereareiam.socialismus.platform.velocity.listener.connection;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import me.whereareiam.socialismus.input.container.PlayerContainerService;
import me.whereareiam.socialismus.output.listener.DynamicListener;

@Singleton
public class PlayerQuitListener implements DynamicListener<DisconnectEvent> {
    private final PlayerContainerService playerContainer;

    @Inject
    public PlayerQuitListener(PlayerContainerService playerContainer) {
        this.playerContainer = playerContainer;
    }

    public void onEvent(DisconnectEvent event) {
        playerContainer.removePlayer(event.getPlayer().getUniqueId());
    }
}
