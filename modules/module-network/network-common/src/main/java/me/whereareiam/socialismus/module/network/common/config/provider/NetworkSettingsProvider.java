package me.whereareiam.socialismus.module.network.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.module.network.common.config.NetworkSettings;
import me.whereareiam.socialismus.module.network.common.config.defaults.NetworkSettingsDefaults;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class NetworkSettingsProvider extends ConfigProvider<NetworkSettings> {
	@Inject
	public NetworkSettingsProvider(
			@Named("workingPath") Path workingPath,
			Registry<Reloadable> reloadableRegistry
	) {
		super(workingPath, "settings", NetworkSettings.class, reloadableRegistry);
	}

	@Override
	protected Configura configura() {
		return super.configura().withDefaults(NetworkSettingsDefaults.class);
	}
}
