package me.whereareiam.socialismus.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.common.config.defaults.CommandsDefaults;
import me.whereareiam.socialismus.model.config.Commands;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class CommandsProvider extends DefaultConfigProvider<Commands> {
	@Inject
	public CommandsProvider(@Named("dataPath") Path dataPath, Registry<Reloadable> registry) {
		super(dataPath, "commands", Commands.class, registry);
	}

	@Override
	protected Configura configura() {
		return versioned(super.configura().withDefaults(CommandsDefaults.class), Commands.class);
	}
}
