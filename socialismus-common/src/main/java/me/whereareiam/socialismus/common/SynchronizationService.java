package me.whereareiam.socialismus.common;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.common.chat.worker.formatted.SynchronizationPublisher;
import me.whereareiam.socialismus.common.sync.ChatHistoryNetworkBridge;
import me.whereareiam.socialismus.common.sync.ChatNetworkBridge;
import me.whereareiam.socialismus.common.sync.CrossPlayerSyncBridge;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SynchronizationService {
	private final Injector injector;
	private final Provider<Settings> settings;

	public void initialize() {
		var sync = settings.get().getSynchronization();
		if (!sync.isEnabled()) return;

		injector.getInstance(ChatNetworkBridge.class).initialize();

		injector.getInstance(ChatHistoryNetworkBridge.class).initialize();

		if (sync.isCrossPlayerSync())
			injector.getInstance(CrossPlayerSyncBridge.class).initialize();

		injector.getInstance(SynchronizationPublisher.class);
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
