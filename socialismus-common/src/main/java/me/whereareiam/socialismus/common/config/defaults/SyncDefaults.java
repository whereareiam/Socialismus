package me.whereareiam.socialismus.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.model.config.Sync;

import java.util.UUID;

@Singleton
public class SyncDefaults implements DefaultsProvider<Sync> {
	@Override
	public Sync supply(Sync sync) {
		sync.setEnabled(false);
		sync.setServer(UUID.randomUUID().toString());
		sync.setUseRealServerName(false);
		sync.setCrossPlayerSync(true);

		return sync;
	}
}
