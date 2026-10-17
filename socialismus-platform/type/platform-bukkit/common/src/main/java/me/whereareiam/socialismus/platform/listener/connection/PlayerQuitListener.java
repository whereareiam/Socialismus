package me.whereareiam.socialismus.platform.listener.connection;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.listener.DynamicListener;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import org.bukkit.event.player.PlayerQuitEvent;

@Singleton
public class PlayerQuitListener implements DynamicListener<PlayerQuitEvent> {
	private final Provider<Settings> settings;
	private final PlayerRegistry playerRegistry;

	@Inject
	public PlayerQuitListener(Provider<Settings> settings, PlayerRegistry playerRegistry) {
		this.settings = settings;
		this.playerRegistry = playerRegistry;
	}

	public void onEvent(PlayerQuitEvent event) {
		if (settings.get().getMisc().isDisableQuitNotification()) event.setQuitMessage(null);

		playerRegistry.removePlayerData(event.getPlayer().getUniqueId());
	}
}
