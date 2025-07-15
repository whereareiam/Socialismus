package me.whereareiam.socialismus.platform.paper.listener.connection;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.input.container.PlayerContainerService;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.output.listener.DynamicListener;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PlayerJoinListener implements DynamicListener<PlayerJoinEvent> {
	private final Provider<Settings> settings;
	private final PlayerContainerService containerService;

	public void onEvent(PlayerJoinEvent event) {
		if (settings.get().getMisc().isDisableJoinNotification())
			event.joinMessage(null);

		Player player = event.getPlayer();
		containerService.updatePlayer(
				event.getPlayer().getUniqueId(),
				containerService.getPlayer(event.getPlayer().getUniqueId()).map(dummyPlayer ->
						dummyPlayer.toBuilder()
								.location(player.getWorld().getName())
								.locale(player.locale())
								.audience(player)
								.build()
				).orElse(null)
		);
	}
}
