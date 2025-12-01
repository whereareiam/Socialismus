package me.whereareiam.socialismus.common;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.common.chat.worker.formatted.SynchronizationPublisher;
import me.whereareiam.socialismus.common.sync.ChatHistoryNetworkBridge;
import me.whereareiam.socialismus.common.sync.ChatNetworkBridge;
import me.whereareiam.socialismus.common.sync.CrossPlayerSyncBridge;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SynchronizationService {
	private final Injector injector;
	private final Provider<Settings> settings;

	public void initialize() {
		if (!Constants.Synchronization.SYNCHRONIZATION) return;

		injector.getInstance(ChatNetworkBridge.class).initialize();

		injector.getInstance(ChatHistoryNetworkBridge.class).initialize();

		if (Constants.Synchronization.CROSS_PLAYER_SYNC)
			injector.getInstance(CrossPlayerSyncBridge.class).initialize();

		injector.getInstance(SynchronizationPublisher.class);
	}

	public void applyTo(SocialismusPlayer player, String actualServerName) {
		var sync = settings.get().getSynchronization();
		if (!Constants.Synchronization.SYNCHRONIZATION) return;

		if (sync.isUseRealServerName() && actualServerName != null) {
			player.setServer(actualServerName);
			return;
		}

		player.setServer(Constants.Synchronization.IDENTIFIER);
	}
}
