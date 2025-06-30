package me.whereareiam.socialismus.common;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SynchronizationService {
	private final Provider<Settings> settings;

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
