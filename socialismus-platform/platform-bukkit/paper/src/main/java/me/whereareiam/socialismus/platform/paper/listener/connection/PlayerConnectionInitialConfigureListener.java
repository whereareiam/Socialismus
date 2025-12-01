package me.whereareiam.socialismus.platform.paper.listener.connection;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.papermc.paper.event.connection.configuration.PlayerConnectionInitialConfigureEvent;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.input.container.PlayerContainerService;
import me.whereareiam.socialismus.model.player.DummyPlayer;
import me.whereareiam.socialismus.output.PlatformInteractor;
import me.whereareiam.socialismus.output.listener.DynamicListener;
import me.whereareiam.socialismus.common.SynchronizationService;
import net.kyori.adventure.audience.Audience;

@Singleton
@SuppressWarnings("UnstableApiUsage")
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PlayerConnectionInitialConfigureListener implements DynamicListener<PlayerConnectionInitialConfigureEvent> {
	private final PlayerContainerService containerService;
	private final PlatformInteractor interactor;
	private final SynchronizationService syncService;

	public void onEvent(PlayerConnectionInitialConfigureEvent event) {
		PlayerProfile playerProfile = event.getConnection().getProfile();
		Audience audience = event.getConnection().getAudience();

		DummyPlayer dummyPlayer = DummyPlayer.builder()
				.username(playerProfile.getName())
				.uniqueId(playerProfile.getId())
				// helpers
				.audience(audience)
				.interactor(interactor)
				.build();

		syncService.applyTo(dummyPlayer, null);
		containerService.addPlayer(dummyPlayer);
	}
}
