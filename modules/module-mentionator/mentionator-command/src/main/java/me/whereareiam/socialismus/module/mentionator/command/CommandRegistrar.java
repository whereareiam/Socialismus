package me.whereareiam.socialismus.module.mentionator.command;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorCommands;
import me.whereareiam.socialismus.module.mentionator.command.executor.MentionsCommand;
import me.whereareiam.socialismus.service.CommandService;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CommandRegistrar {
	private final Injector injector;
	private final CommandService commandService;
	private final Provider<MentionatorCommands> commands;

	public void registerCommands() {
		commandService.registerCommandInstances(
				commands.get().getCommands(),
				injector.getInstance(MentionsCommand.class)
		);
	}
}
