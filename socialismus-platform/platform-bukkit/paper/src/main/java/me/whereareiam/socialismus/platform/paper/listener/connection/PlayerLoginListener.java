package me.whereareiam.socialismus.platform.paper.listener.connection;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.input.container.PlayerContainerService;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.listener.DynamicListener;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;

@Singleton
public class PlayerLoginListener implements DynamicListener<PlayerLoginEvent> {
    private final PlayerContainerService containerService;

    @Inject
    public PlayerLoginListener(PlayerContainerService containerService) {
        this.containerService = containerService;
    }

    public void onEvent(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        DummyPlayer dummyPlayer = DummyPlayer.builder()
                .username(player.getName())
                .uniqueId(player.getUniqueId())
                .audience(player)
                .location(player.getWorld().getName())
                .locale(player.locale())
                .build();

        containerService.addPlayer(dummyPlayer);
    }
}
