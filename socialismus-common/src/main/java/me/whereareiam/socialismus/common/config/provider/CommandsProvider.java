package me.whereareiam.socialismus.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.input.registry.Registry;
import me.whereareiam.socialismus.model.CommandEntity;
import me.whereareiam.socialismus.model.config.Commands;
import me.whereareiam.socialismus.common.config.template.CommandsTemplate;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class CommandsProvider extends DefaultConfigProvider<Map<String, CommandEntity>> implements Registry<Map<String, CommandEntity>> {
	private Map<String, CommandEntity> commands;

	@Inject
	public CommandsProvider(@Named("dataPath") Path dataPath, Registry<Reloadable> registry) {
		super(dataPath, registry);
	}

	@Override
	protected Map<String, CommandEntity> load() {
		commands = new HashMap<>(Config.update(getBasePath().resolve("commands"), Commands.class).getCommands());
		return commands;
	}

	@Override
	protected void registerTemplate() {
		Config.registerTemplate(CommandsTemplate.class);
	}

	@Override
	public Map<String, CommandEntity> get() {
		return super.get();
	}

	@Override
	public void register(Map<String, CommandEntity> commands) {
		this.commands.putAll(commands);
	}
}