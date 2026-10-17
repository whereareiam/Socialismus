package me.whereareiam.socialismus.platform.listener.activity;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.listener.DynamicListener;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import org.bukkit.event.player.PlayerChangedWorldEvent;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PlayerWorldChangeListener implements DynamicListener<PlayerChangedWorldEvent> {
	private final PlayerRegistry playerRegistry;

	public void onEvent(PlayerChangedWorldEvent event) {
		playerRegistry.getPlayerData(event.getPlayer().getUniqueId()).ifPresent(
				player -> player.setLocation(event.getPlayer().getWorld().getName())
		);
	}
}
