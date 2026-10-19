package me.whereareiam.socialismus.module.link.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.module.link.common.config.LinkSettings;
import me.whereareiam.socialismus.module.link.common.config.defaults.LinkSettingsDefaults;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class LinkSettingsProvider extends ConfigProvider<LinkSettings> {
	@Inject
	public LinkSettingsProvider(
			@Named("workingPath") Path workingPath,
			Registry<Reloadable> reloadableRegistry
	) {
		super(workingPath, "settings", LinkSettings.class, reloadableRegistry);
	}

	@Override
	protected Configura configura() {
		return super.configura().withDefaults(LinkSettingsDefaults.class);
	}
}
