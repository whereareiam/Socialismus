package me.whereareiam.socialismus.module.bubbler.command;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerCommands;
import me.whereareiam.socialismus.module.bubbler.command.executor.BubbleCommand;
import me.whereareiam.socialismus.service.CommandService;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CommandRegistrar {
	private final Injector injector;
	private final CommandService commandService;
	private final Provider<BubblerCommands> commands;

	public void registerCommands() {
		// Instantiate command using module's injector (has access to module-specific bindings)
		BubbleCommand bubbleCommand = injector.getInstance(BubbleCommand.class);

		// Register with pre-instantiated objects
		commandService.registerCommandInstances(commands.get().getCommands(), bubbleCommand);
	}
}
