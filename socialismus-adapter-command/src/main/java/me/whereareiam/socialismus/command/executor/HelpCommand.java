package me.whereareiam.socialismus.command.executor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.Serializer;
import me.whereareiam.socialismus.api.model.CommandEntity;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.command.CommandBase;
import me.whereareiam.socialismus.api.output.command.CommandCooldown;
import me.whereareiam.socialismus.command.builder.HelpBuilder;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotation.specifier.Range;
import org.incendo.cloud.annotations.*;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class HelpCommand extends CommandBase {
	private static final String COMMAND_NAME = "help";
	private final Provider<Map<String, CommandEntity>> commands;
	private final Provider<CommandManager<DummyPlayer>> commandManager;
	private final HelpBuilder helpBuilder;

	@Inject
	public HelpCommand(
			Provider<Map<String, CommandEntity>> commands,
			Provider<CommandManager<DummyPlayer>> commandManager,
			HelpBuilder helpBuilder
	) {
		super(COMMAND_NAME);
		this.commands = commands;

		this.commandManager = commandManager;
		this.helpBuilder = helpBuilder;
	}

	@Command("%command." + COMMAND_NAME)
	@CommandDescription("%description." + COMMAND_NAME)
	@CommandCooldown("%cooldown." + COMMAND_NAME)
	@Permission("%permission." + COMMAND_NAME)
	public void onCommand(DummyPlayer dummyPlayer, @Range(min = "1") @Default("1") @Argument(value = "page") int page) {
		Collection<org.incendo.cloud.Command<DummyPlayer>> allowedCommands = commandManager.get().commands()
				.stream()
				.filter(command -> dummyPlayer.getUsername() != null || commandManager.get().hasPermission(dummyPlayer, command.commandPermission().permissionString()))
				.collect(Collectors.toList());

		dummyPlayer.sendMessage(Serializer.serialize(dummyPlayer, helpBuilder.buildHelpMessage(allowedCommands, page)));
	}

	@Override
	public CommandEntity getCommandEntity() {
		return commands.get().get(COMMAND_NAME);
	}
}