package me.whereareiam.socialismus.platform.paper.listener.connection;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.input.container.PlayerContainerService;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.PlatformInteractor;
import me.whereareiam.socialismus.api.output.listener.DynamicListener;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PlayerLoginListener implements DynamicListener<PlayerLoginEvent> {
	private final PlayerContainerService containerService;
	private final PlatformInteractor interactor;

	public void onEvent(PlayerLoginEvent event) {
		Player player = event.getPlayer();

		DummyPlayer dummyPlayer = DummyPlayer.builder()
				.username(player.getName())
				.uniqueId(player.getUniqueId())
				.location(player.getWorld().getName())
				.locale(player.locale())
				// helpers
				.audience(player)
				.interactor(interactor)
				.build();

		containerService.addPlayer(dummyPlayer);
	}
}
