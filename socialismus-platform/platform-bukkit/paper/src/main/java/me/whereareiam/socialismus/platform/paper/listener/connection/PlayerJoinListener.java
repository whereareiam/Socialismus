package me.whereareiam.socialismus.platform.paper.listener.connection;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.output.listener.DynamicListener;
import org.bukkit.event.player.PlayerJoinEvent;

@Singleton
public class PlayerJoinListener implements DynamicListener<PlayerJoinEvent> {
    private final Provider<Settings> settings;

    @Inject
    public PlayerJoinListener(Provider<Settings> settings) {
        this.settings = settings;
    }

    public void onEvent(PlayerJoinEvent event) {
        if (settings.get().getMisc().isDisableJoinNotification()) event.joinMessage(null);
    }
}
