package me.whereareiam.socialismus.platform.velocity.listener.connection;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import me.whereareiam.socialismus.output.listener.DynamicListener;
import me.whereareiam.socialismus.registry.PlayerRegistry;

@Singleton
public class PlayerQuitListener implements DynamicListener<DisconnectEvent> {
    private final PlayerRegistry playerRegistry;

    @Inject
    public PlayerQuitListener(PlayerRegistry playerRegistry) {
        this.playerRegistry = playerRegistry;
    }

    public void onEvent(DisconnectEvent event) {
        playerRegistry.removePlayerData(event.getPlayer().getUniqueId());
    }
}
