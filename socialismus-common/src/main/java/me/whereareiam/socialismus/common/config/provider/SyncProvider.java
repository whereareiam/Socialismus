package me.whereareiam.socialismus.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.common.config.defaults.SyncDefaults;
import me.whereareiam.socialismus.model.config.Sync;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class SyncProvider extends DefaultConfigProvider<Sync> {
	@Inject
	public SyncProvider(@Named("dataPath") Path dataPath, Registry<Reloadable> registry) {
		super(dataPath, "sync", Sync.class, registry);
	}

	@Override
	protected Configura configura() {
		return super.configura().withDefaults(SyncDefaults.class);
	}
}
