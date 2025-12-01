package me.whereareiam.socialismus.platform.bukkit.listener.connection;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.input.container.PlayerContainerService;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.output.listener.DynamicListener;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.event.player.PlayerJoinEvent;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PlayerJoinListener implements DynamicListener<PlayerJoinEvent> {
	private final Provider<Settings> settings;
	private final PlayerContainerService containerService;
	private final Provider<BukkitAudiences> audiences;

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
