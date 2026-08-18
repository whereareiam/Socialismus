package me.whereareiam.socialismus.module.essentials.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.module.essentials.api.model.config.EssentialsCommands;
import me.whereareiam.socialismus.module.essentials.common.config.defaults.EssentialsCommandsDefaults;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class EssentialsCommandsProvider extends ConfigProvider<EssentialsCommands> {
	@Inject
	public EssentialsCommandsProvider(
			@Named("workingPath") Path workingPath,
			Registry<Reloadable> reloadableRegistry
	) {
		super(workingPath, "commands", EssentialsCommands.class, reloadableRegistry);
	}

	@Override
	protected Configura configura() {
		return versioned(super.configura().withDefaults(EssentialsCommandsDefaults.class), EssentialsCommands.class);
	}
}
