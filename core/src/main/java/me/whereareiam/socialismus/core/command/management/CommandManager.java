package me.whereareiam.socialismus.core.command.management;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.RootCommand;
import co.aikar.locales.MessageKey;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.command.base.CommandBase;
import me.whereareiam.socialismus.core.config.message.MessagesConfig;

import java.util.*;

@Singleton
@SuppressWarnings("rawtypes")
public class CommandManager {
	private final BukkitCommandManager bukkitCommandManager;
	private final MessagesConfig messagesConfig;

	private final Map<RootCommand, Set<RegisteredCommand>> commandsMap = new HashMap<>();

	private int commandCount = 0;

	@Inject
	public CommandManager(BukkitCommandManager bukkitCommandManager, MessagesConfig messagesConfig) {
		this.bukkitCommandManager = bukkitCommandManager;
		this.messagesConfig = messagesConfig;

		addTranslations();
	}

	public void registerCommand(CommandBase command) {
		if (command.isEnabled()) {
			command.addReplacements();
			bukkitCommandManager.registerCommand(command);
		}
		commandCount++;
	}

	public void setCommands() {
		Collection<RootCommand> allCommands = bukkitCommandManager.getRegisteredRootCommands();
		Set<String> uniqueParentClassNames = new HashSet<>();

		for (RootCommand rootCommand : allCommands) {
			String parentClassName = rootCommand.getDefCommand().getName();

			if (!parentClassName.equals("chatcommandtemplate") && !uniqueParentClassNames.contains(parentClassName)) {
				uniqueParentClassNames.add(parentClassName);

				HashSet<RegisteredCommand> uniqueSubCommands = new HashSet<>();
				rootCommand.getSubCommands().entries().forEach(entry -> {
					RegisteredCommand registeredCommand = entry.getValue();

					if (!uniqueSubCommands.contains(registeredCommand) && !registeredCommand.equals(rootCommand.getDefaultRegisteredCommand())) {
						uniqueSubCommands.add(registeredCommand);
					}
				});

				commandsMap.put(rootCommand, uniqueSubCommands);
			}
		}
	}

	public void addTranslations() {
		addTranslation(messagesConfig.commands.unknownCommand, "acf-core.permission_denied");
		addTranslation(messagesConfig.commands.unknownCommand, "acf-core.permission_denied_parameter");
		addTranslation(messagesConfig.commands.wrongSyntax, "acf-core.invalid_syntax");
		addTranslation(messagesConfig.commands.unknownCommand, "acf-core.unknown_command");
		addTranslation(messagesConfig.commands.errorOccurred, "acf-core.error_performing_command");
		addTranslation(messagesConfig.commands.missingArgument, "acf-core.please_specify_one_of");
	}

	private void addTranslation(String message, String acfKey) {
		bukkitCommandManager.getLocales().addMessage(Locale.ENGLISH, MessageKey.of(acfKey), message);
	}

	public int getCommandCount() {
		return commandCount;
	}

	public Map<RootCommand, Set<RegisteredCommand>> getAllCommands() {
		return commandsMap;
	}
}
