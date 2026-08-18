package me.whereareiam.socialismus.module.essentials.command;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.service.CommandService;
import me.whereareiam.socialismus.module.essentials.api.model.config.EssentialsCommands;
import me.whereareiam.socialismus.module.essentials.command.executor.FeaturesCommand;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class CommandRegistrar {
	private final Injector injector;
	private final CommandService commandService;
	private final Provider<EssentialsCommands> commands;

	public void registerCommands() {
		// Instantiate command using module's injector
		FeaturesCommand featuresCommand = injector.getInstance(FeaturesCommand.class);

		// Register with pre-instantiated objects
		commandService.registerCommandInstances(commands.get().getCommands(), featuresCommand);
	}
}
