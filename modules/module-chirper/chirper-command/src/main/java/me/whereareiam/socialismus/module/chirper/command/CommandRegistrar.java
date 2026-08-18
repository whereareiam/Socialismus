package me.whereareiam.socialismus.module.chirper.command;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.chirper.api.model.config.ChirperCommands;
import me.whereareiam.socialismus.module.chirper.command.executor.AnnounceCommand;
import me.whereareiam.socialismus.service.CommandService;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CommandRegistrar {
	private final Injector injector;
	private final CommandService commandService;
	private final Provider<ChirperCommands> commands;

	public void registerCommands() {
		// Instantiate command using module's injector (has access to module-specific bindings)
		AnnounceCommand announceCommand = injector.getInstance(AnnounceCommand.class);
		
		// Register with pre-instantiated objects
		commandService.registerCommandInstances(
				commands.get().getCommands(),
				announceCommand
		);
	}
}
