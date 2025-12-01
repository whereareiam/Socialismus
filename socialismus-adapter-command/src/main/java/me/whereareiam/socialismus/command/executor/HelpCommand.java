package me.whereareiam.socialismus.command.executor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.commandant.Help;
import me.whereareiam.commandant.Pagination;
import me.whereareiam.commandant.annotation.Definition;
import me.whereareiam.commandant.builder.HelpBuilder;
import me.whereareiam.commandant.model.CommandDefinition;
import me.whereareiam.keystone.Actor;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.input.registry.Registry;
import me.whereareiam.socialismus.model.config.Commands;
import me.whereareiam.socialismus.model.config.message.Messages;
import net.kyori.adventure.text.Component;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotation.specifier.Range;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Default;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class HelpCommand implements Reloadable {
	private final Provider<Commands> commandsProvider;
	private final Provider<Messages> messagesProvider;
	private final Provider<CommandManager<Actor>> commandManagerProvider;

	private HelpBuilder<Actor> helpBuilder;

	@Inject
	public HelpCommand(
			@NotNull Provider<Commands> commandsProvider,
			@NotNull Provider<Messages> messagesProvider,
			@NotNull Provider<CommandManager<Actor>> commandManagerProvider,
			@NotNull Registry<Reloadable> reloadableRegistry
	) {
		this.commandsProvider = commandsProvider;
		this.messagesProvider = messagesProvider;
		this.commandManagerProvider = commandManagerProvider;
		reloadableRegistry.register(this);
	}

	@Definition("help")
	@Command("intercept help [page]")
	public void command(@NotNull Actor sender, @Argument("page") @Default("1") @Range(min = "1") int page) {
		// Get help message
		String helpMessage = getHelpBuilder().build(getFilteredCommands(sender), page);

		// Send formatted message using serializer
		Component component = Serializer.serialize(sender, helpMessage);
		sender.sendMessage(component);
	}

	/**
	 * Gets the help builder, creating it lazily on first use.
	 *
	 * @return The help builder instance
	 */
	@NotNull
	private HelpBuilder<Actor> getHelpBuilder() {
		if (helpBuilder == null) {
			Messages messages = messagesProvider.get();

			helpBuilder = Help.create(
					messages.getCommands().getHelp(),
					collectArgumentDescriptions(),
					Pagination.create(messages.getCommands().getPagination()),
					messages.getCommands().getHelp().getCommandsPerPage(),
					true
			);
		}
		return helpBuilder;
	}

	@NotNull
	private Map<String, String> collectArgumentDescriptions() {
		return commandsProvider.get().getCommands().values().stream()
				.map(CommandDefinition::getArguments)
				.filter(map -> map != null && !map.isEmpty())
				.flatMap(map -> map.entrySet().stream())
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(existing, replacement) -> replacement
				));
	}

	/**
	 * Gets all commands filtered by sender's permissions.
	 *
	 * @param sender The command sender
	 * @return Collection of commands the sender has permission to see
	 */
	@NotNull
	private Collection<org.incendo.cloud.Command<Actor>> getFilteredCommands(@NotNull Actor sender) {
		CommandManager<Actor> commandManager = commandManagerProvider.get();

		return commandManager.commands()
				.stream()
				.filter(command -> commandManager.hasPermission(sender, command.commandPermission().permissionString()))
				.collect(Collectors.toList());
	}

	@Override
	public void reload() {
		helpBuilder = null;
	}
}