package me.whereareiam.socialismus.platform.velocity.listener.connection;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.input.container.PlayerContainerService;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.PlatformInteractor;
import me.whereareiam.socialismus.api.output.listener.DynamicListener;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PlayerJoinListener implements DynamicListener<PlayerChooseInitialServerEvent> {
	private final PlayerContainerService playerContainer;
	private final PlatformInteractor interactor;

	public void onEvent(PlayerChooseInitialServerEvent event) {
		Player player = event.getPlayer();

		DummyPlayer dummyPlayer = DummyPlayer.builder()
				.username(player.getUsername())
				.uniqueId(player.getUniqueId())
				.location(event.getInitialServer().map(s -> s.getServerInfo().getName()).orElse(null))
				.locale(player.getEffectiveLocale())
				// helpers
				.audience(player)
				.interactor(interactor)
				.build();

		playerContainer.addPlayer(dummyPlayer);
	}
}
