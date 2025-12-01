package me.whereareiam.socialismus.platform.bukkit.listener.connection;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.output.listener.DynamicListener;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PlayerJoinListener implements DynamicListener<PlayerJoinEvent> {
	private final Provider<Settings> settings;
	private final PlayerRegistry playerRegistry;

	public void onEvent(PlayerJoinEvent event) {
		if (settings.get().getMisc().isDisableJoinNotification())
			event.setJoinMessage(null);

		Player player = event.getPlayer();
		playerRegistry.getPlayerData(player.getUniqueId()).ifPresent(
				socialismusPlayer -> socialismusPlayer.setLocation(player.getWorld().getName())
		);
	}
}
