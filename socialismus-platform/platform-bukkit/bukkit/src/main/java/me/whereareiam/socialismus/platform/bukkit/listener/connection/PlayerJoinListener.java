package me.whereareiam.socialismus.platform.bukkit.listener.connection;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.input.container.PlayerContainerService;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.output.listener.DynamicListener;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.event.player.PlayerJoinEvent;

@Singleton
public class PlayerJoinListener implements DynamicListener<PlayerJoinEvent> {
    private final Provider<Settings> settings;
    private final PlayerContainerService containerService;
    private final Provider<BukkitAudiences> audiences;

    @Inject
    public PlayerJoinListener(Provider<Settings> settings, PlayerContainerService containerService, Provider<BukkitAudiences> audiences) {
        this.settings = settings;
        this.containerService = containerService;
        this.audiences = audiences;
    }

    public void onEvent(PlayerJoinEvent event) {
        final BukkitAudiences audiences = this.audiences.get();
        if (settings.get().getMisc().isDisableJoinNotification()) event.setJoinMessage(null);

        containerService.updatePlayer(
                event.getPlayer().getUniqueId(),
                containerService.getPlayer(event.getPlayer().getUniqueId()).map(player ->
                        player.toBuilder().audience(audiences.player(event.getPlayer())).build()
                ).orElse(null)
        );
    }
}
