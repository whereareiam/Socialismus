package me.whereareiam.socialismus.module.essentials.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.module.essentials.api.model.config.EssentialsMessages;
import me.whereareiam.socialismus.module.essentials.common.config.defaults.EssentialsMessagesDefaults;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class EssentialsMessagesProvider extends ConfigProvider<EssentialsMessages> {
	@Inject
	public EssentialsMessagesProvider(
			@Named("workingPath") Path workingPath,
			Registry<Reloadable> reloadableRegistry
	) {
		super(workingPath, "messages", EssentialsMessages.class, reloadableRegistry);
	}

	@Override
	protected Configura configura() {
		return versioned(super.configura().withDefaults(EssentialsMessagesDefaults.class), EssentialsMessages.class);
	}
}
