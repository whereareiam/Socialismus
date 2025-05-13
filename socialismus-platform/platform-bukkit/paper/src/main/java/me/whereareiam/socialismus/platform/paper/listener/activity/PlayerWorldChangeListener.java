package me.whereareiam.socialismus.platform.paper.listener.activity;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.input.container.PlayerContainerService;
import me.whereareiam.socialismus.api.output.listener.DynamicListener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

@Singleton
public class PlayerWorldChangeListener implements DynamicListener<PlayerChangedWorldEvent> {
    private final PlayerContainerService playerContainer;

    @Inject
    public PlayerWorldChangeListener(PlayerContainerService playerContainer) {
        this.playerContainer = playerContainer;
    }

    public void onEvent(PlayerChangedWorldEvent event) {
        playerContainer.getPlayer(event.getPlayer().getUniqueId()).ifPresent(
                player -> player.setLocation(event.getPlayer().getWorld().getName())
        );
    }
}
