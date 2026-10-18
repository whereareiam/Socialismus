package me.whereareiam.socialismus.module.dialogue.command;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueCommands;
import me.whereareiam.socialismus.module.dialogue.command.executor.MessageCommand;
import me.whereareiam.socialismus.module.dialogue.command.executor.ReplyCommand;
import me.whereareiam.socialismus.service.CommandService;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CommandRegistrar {
	private final Injector injector;
	private final CommandService commandService;
	private final Provider<DialogueCommands> commands;

	public void registerCommands() {
		commandService.registerCommandInstances(
				commands.get().getCommands(),
				injector.getInstance(MessageCommand.class),
				injector.getInstance(ReplyCommand.class)
		);
	}
}
