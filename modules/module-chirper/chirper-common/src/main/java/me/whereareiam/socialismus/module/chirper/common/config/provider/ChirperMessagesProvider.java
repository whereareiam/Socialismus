package me.whereareiam.socialismus.module.chirper.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.module.chirper.api.model.config.ChirperMessages;
import me.whereareiam.socialismus.module.chirper.common.config.defaults.ChirperMessagesDefaults;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class ChirperMessagesProvider extends ConfigProvider<ChirperMessages> {
	@Inject
	public ChirperMessagesProvider(
			@Named("workingPath") Path workingPath,
			Registry<Reloadable> registry
	) {
		super(workingPath, "messages", ChirperMessages.class, registry);
	}

	@Override
	protected Configura configura() {
		return versioned(super.configura().withDefaults(ChirperMessagesDefaults.class), ChirperMessages.class);
	}
}
