package me.whereareiam.socialismus.platform.bukkit.listener.connection;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.input.container.PlayerContainerService;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.output.listener.DynamicListener;
import org.bukkit.event.player.PlayerQuitEvent;

@Singleton
public class PlayerQuitListener implements DynamicListener<PlayerQuitEvent> {
    private final Provider<Settings> settings;
    private final PlayerContainerService containerService;

    @Inject
    public PlayerQuitListener(Provider<Settings> settings, PlayerContainerService containerService) {
        this.settings = settings;
        this.containerService = containerService;
    }

    public void onEvent(PlayerQuitEvent event) {
        if (settings.get().getMisc().isDisableQuitNotification()) event.setQuitMessage(null);

        containerService.removePlayer(event.getPlayer().getUniqueId());
    }
}
