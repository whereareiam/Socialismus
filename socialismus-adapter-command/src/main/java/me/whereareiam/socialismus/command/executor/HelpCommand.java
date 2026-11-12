package me.whereareiam.socialismus.command.executor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.commandant.Help;
import me.whereareiam.commandant.Pagination;
import me.whereareiam.commandant.annotation.Definition;
import me.whereareiam.commandant.builder.HelpBuilder;
import me.whereareiam.keystone.Actor;
import me.whereareiam.socialismus.command.help.HelpRenderer;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.model.CommandDefinition;
import me.whereareiam.socialismus.model.config.Commands;
import me.whereareiam.socialismus.model.config.message.Messages;
import me.whereareiam.socialismus.service.CommandService;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotation.specifier.Range;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Default;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class HelpCommand implements EventListener {
	private static final String HOST = "Socialismus";

	private final Provider<Commands> commandsProvider;
	private final Provider<Messages> messagesProvider;
	private final Provider<CommandManager<Actor>> commandManagerProvider;
	private final CommandService commandService;

	private HelpBuilder<Actor> helpBuilder;
	private HelpRenderer helpRenderer;
	private Map<String, String> cachedArgumentDescriptions;

	@Inject
	public HelpCommand(
			@NotNull Provider<Commands> commandsProvider,
			@NotNull Provider<Messages> messagesProvider,
			@NotNull Provider<CommandManager<Actor>> commandManagerProvider,
			@NotNull CommandService commandService,
			@NotNull EventManager eventManager
	) {
		this.commandsProvider = commandsProvider;
		this.messagesProvider = messagesProvider;
		this.commandManagerProvider = commandManagerProvider;
		this.commandService = commandService;
		eventManager.register(this);
	}

	@Definition("help")
	@Command("socialismus help [page]")
	public void command(@NotNull Actor sender, @Argument("page") @Default("1") @Range(min = "1") int page) {
		sender.sendMessage(getHelpRenderer().render(sender, getHelpBuilder().build(getFilteredCommands(sender), page)));
	}

	/**
	 * Gets the help builder, creating it lazily on first use.
	 *
	 * @return The help builder instance
	 */
	@NotNull
	private HelpBuilder<Actor> getHelpBuilder() {
		if (helpBuilder == null) {
			Commands.Behavior behavior = commandsProvider.get().getBehavior();
			String pageCommand = pageCommand();

			helpBuilder = Help.<Actor>builder(HelpRenderer.PREFIX, HelpRenderer.ARGUMENTS)
					.host(HOST)
					.argumentNames(getArgumentDescriptions())
					.pagination(Pagination.builder(HelpRenderer.PREFIX + '.' + HelpBuilder.PAGINATION, HelpRenderer.ARGUMENTS)
							.pageCommand(page -> pageCommand + page)
							.showIfOnePage(behavior.getPagination().isShowIfOnePage())
							.showPreviousOnFirst(behavior.getPagination().isShowPreviousOnFirst())
							.showNextOnLast(behavior.getPagination().isShowNextOnLast())
							.build())
					.itemsPerPage(behavior.getHelp().getCommandsPerPage())
					.sortAlphabetically(true)
					.build();
		}
		return helpBuilder;
	}

	@NotNull
	private HelpRenderer getHelpRenderer() {
		if (helpRenderer == null) {
			Messages messages = messagesProvider.get();
			helpRenderer = HelpRenderer.of(messages.getCommands().getHelp(), messages.getCommands().getPagination());
		}
		return helpRenderer;
	}

	// The command that opens a page of help, by the first alias of the main command and of help.
	@NotNull
	private String pageCommand() {
		Map<String, CommandDefinition> definitions = commandsProvider.get().getCommands();

		return "/" + firstAlias(definitions.get("main"), "socialismus") + " " + firstAlias(definitions.get("help"), "help") + " ";
	}

	@NotNull
	private static String firstAlias(@Nullable CommandDefinition definition, @NotNull String fallback) {
		List<String> aliases = definition != null ? definition.getAliases() : null;

		return aliases == null || aliases.isEmpty() ? fallback : aliases.getFirst();
	}

	@NotNull
	private Map<String, String> getArgumentDescriptions() {
		// Cache argument descriptions to avoid rebuilding on every help builder creation
		if (cachedArgumentDescriptions == null)
			cachedArgumentDescriptions = collectArgumentDescriptions();

		return cachedArgumentDescriptions;
	}

	@NotNull
	private Map<String, String> collectArgumentDescriptions() {
		// Collect from all registered definitions (core + modules)
		return commandService.getRegisteredDefinitions().values().stream()
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

	@SocialisticEvent
	public void onReloaded(PluginReloadedEvent event) {
		helpBuilder = null;
		helpRenderer = null;
		cachedArgumentDescriptions = null;
	}
}
