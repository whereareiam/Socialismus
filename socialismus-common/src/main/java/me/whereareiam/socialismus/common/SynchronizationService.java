package me.whereareiam.socialismus.common;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.common.sync.ChatHistoryNetworkBridge;
import me.whereareiam.socialismus.common.sync.ChatNetworkBridge;
import me.whereareiam.socialismus.common.sync.CrossPlayerSyncBridge;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SynchronizationService {
	private final Provider<Settings> settings;

	private final ChatNetworkBridge chatNetworkBridge;
	private final ChatHistoryNetworkBridge chatHistoryNetworkBridge;
	private final CrossPlayerSyncBridge crossPlayerSyncBridge;

	public void initialize() {
		var sync = settings.get().getSynchronization();
		if (!sync.isEnabled()) return;

		chatNetworkBridge.initialize();
		chatHistoryNetworkBridge.initialize();

		if (sync.isCrossPlayerSync())
			crossPlayerSyncBridge.initialize();
	}

	public void applyTo(DummyPlayer player, String actualServerName) {
		var sync = settings.get().getSynchronization();
		if (!sync.isEnabled()) return;

		if (sync.isUseRealServerName() && actualServerName != null) {
			player.setServer(actualServerName);
			return;
		}

		player.setServer(sync.getServer());
	}
}
