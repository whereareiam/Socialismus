package me.whereareiam.socialismus.command;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.commandant.Commandant;
import me.whereareiam.commandant.CommandantKeys;
import me.whereareiam.commandant.CommandantSyntaxFormatter;
import me.whereareiam.commandant.annotation.Definition;
import me.whereareiam.commandant.exception.ExceptionHandlerRegistrar;
import me.whereareiam.commandant.exception.format.CloudPermissionFormatters;
import me.whereareiam.commandant.exception.format.ExceptionFormatting;
import me.whereareiam.commandant.model.message.ExceptionMessages;
import me.whereareiam.keystone.Actor;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.command.definition.CommandDefinitionAdapter;
import me.whereareiam.socialismus.command.executor.*;
import me.whereareiam.socialismus.command.suggestion.CrossPlayerProvider;
import me.whereareiam.socialismus.command.suggestion.PlayerSuggestionProvider;
import me.whereareiam.socialismus.model.CommandDefinition;
import me.whereareiam.socialismus.model.config.Commands;
import me.whereareiam.socialismus.model.config.message.Messages;
import me.whereareiam.socialismus.service.CommandService;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.internal.CommandRegistrationHandler;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Implementation of DefaultCommandService for managing and registering commands.
 * Initializes all commands at plugin startup and registers exception handlers.
 */
@Singleton
public class DefaultCommandService implements CommandService {
	private final SerializerEngine serializer;
	private final Provider<Messages> messagesProvider;
	private final Provider<Commands> commandsProvider;
	private final Provider<CommandManager<Actor>> commandManagerProvider;
	private final Injector injector;

	private final Map<String, CommandDefinition> registeredDefinitions = new HashMap<>();
	private AnnotationParser<Actor> annotationParser;

	@Inject
	public DefaultCommandService(
			@NotNull SerializerEngine serializer,
			@NotNull Provider<Messages> messagesProvider,
			@NotNull Provider<Commands> commandsProvider,
			@NotNull Provider<CommandManager<Actor>> commandManagerProvider,
			@NotNull Injector injector
	) {
		this.messagesProvider = messagesProvider;
		this.commandsProvider = commandsProvider;
		this.serializer = serializer;
		this.commandManagerProvider = commandManagerProvider;
		this.injector = injector;

		initialize();
	}

	@Override
	public void registerCommand(@NotNull String key, @NotNull CommandDefinition definition, @NotNull Class<?> commandClass) {
		registerCommandInstance(key, definition, injector.getInstance(commandClass));
	}

	@Override
	public void registerCommands(@NotNull Map<String, CommandDefinition> definitions, @NotNull Class<?>... commandClasses) {
		Object[] instances = new Object[commandClasses.length];
		for (int i = 0; i < commandClasses.length; i++) {
			instances[i] = injector.getInstance(commandClasses[i]);
		}

		registerCommandInstances(definitions, instances);
	}

	@Override
	public void registerCommandInstance(@NotNull String key, @NotNull CommandDefinition definition, @NotNull Object commandInstance) {
		registeredDefinitions.put(key, definition);
		registerInternal(commandInstance);
	}

	@Override
	public void registerCommandInstances(@NotNull Map<String, CommandDefinition> definitions, @NotNull Object... commandInstances) {
		registeredDefinitions.putAll(definitions);
		registerInternal(commandInstances);
	}

	@Override
	public int getCommandCount() {
		return commandManagerProvider.get().commands().size();
	}

	@Override
	public @NotNull Map<String, CommandDefinition> getRegisteredDefinitions() {
		Map<String, CommandDefinition> allDefinitions = new HashMap<>(registeredDefinitions);
		Commands commands = commandsProvider.get();
		if (commands != null && commands.getCommands() != null) {
			commands.getCommands().forEach(allDefinitions::putIfAbsent);
		}

		return allDefinitions;
	}

	public void initialize() {
		CommandManager<Actor> commandManager = commandManagerProvider.get();

		registerSyntaxFormatter(commandManager);
		registerSuggestionProviders(commandManager);
		registerInternal(
				injector.getInstance(MainCommand.class),
				injector.getInstance(HelpCommand.class),
				injector.getInstance(DebugCommand.class),
				injector.getInstance(ReloadCommand.class),
				injector.getInstance(ClearCommand.class)
		);
		registerExceptionHandlers(commandManager);
	}

	private void registerInternal(@NotNull Object @NotNull ... commandInstances) {
		CommandManager<Actor> commandManager = commandManagerProvider.get();
		if (annotationParser == null) {
			annotationParser = createAnnotationParser(commandManager);
		}

		Collection<Command<Actor>> parsed = annotationParser.parse(commandInstances);
		processParsedCommands(parsed, commandManager);
	}

	private void processParsedCommands(
			@NotNull Collection<Command<Actor>> parsedCommands,
			@NotNull CommandManager<Actor> commandManager
	) {
		CommandDefinitionAdapter adapter = new CommandDefinitionAdapter();
		List<String> rootAliases = resolveRootAliases(lookupDefinition("main"));

		for (Command<Actor> command : parsedCommands) {
			String definitionId = command.commandMeta().optional(CommandantKeys.DEFINITION_ID).orElse(null);
			CommandDefinition definition = definitionId != null ? lookupDefinition(definitionId) : null;
			boolean sharedRootCommand = definition != null
					&& ("main".equals(definitionId) || isSubcommand(definition));

			Commandant.process(command, commandManager)
					.withDefinition(definition, adapter, sharedRootCommand ? rootAliases : List.of())
					.register();
		}
	}

	private @NotNull AnnotationParser<Actor> createAnnotationParser(@NotNull CommandManager<Actor> commandManager) {
		AnnotationParser<Actor> parser = new AnnotationParser<>(
				new RecordingCommandManager(commandManager),
				Actor.class
		);
		parser.registerBuilderModifier(
				Definition.class,
				(annotation, builder) -> builder.meta(CommandantKeys.DEFINITION_ID, annotation.value())
		);
		return parser;
	}

	private CommandDefinition lookupDefinition(@NotNull String key) {
		CommandDefinition registered = registeredDefinitions.get(key);
		if (registered != null) return registered;

		Commands commands = commandsProvider.get();
		if (commands == null || commands.getCommands() == null) return null;

		return commands.getCommands().get(key);
	}

	private boolean isSubcommand(@NotNull CommandDefinition definition) {
		String usage = definition.getUsage();
		return usage != null && usage.contains(Serializer.placeholder("command"));
	}

	private @NotNull List<String> resolveRootAliases(@Nullable CommandDefinition definition) {
		if (definition == null || definition.getAliases() == null || definition.getAliases().isEmpty())
			return List.of();

		LinkedHashSet<String> resolved = new LinkedHashSet<>();
		for (String alias : definition.getAliases()) {
			if (alias == null) continue;
			String trimmed = alias.trim();
			if (!trimmed.isEmpty()) resolved.add(trimmed);
		}

		return List.copyOf(resolved);
	}

	/**
	 * Registers suggestion providers using Cloud's AnnotationParser with the real command manager.
	 * This ensures suggestions are registered before commands that reference them.
	 */
	private void registerSuggestionProviders(@NotNull CommandManager<Actor> commandManager) {
		AnnotationParser<Actor> parser = new AnnotationParser<>(commandManager, Actor.class);
		parser.parse(
				injector.getInstance(PlayerSuggestionProvider.class),
				injector.getInstance(CrossPlayerProvider.class)
		);
	}

	private void registerSyntaxFormatter(@NotNull CommandManager<Actor> commandManager) {
		Messages messages = messagesProvider.get();
		if (messages == null || messages.getCommands() == null || messages.getCommands().getHelp() == null) return;

		commandManager.commandSyntaxFormatter(new CommandantSyntaxFormatter<>(
				commandManager,
				collectArgumentDescriptions(),
				messages.getCommands().getHelp().getArgumentFormat(),
				serializer.getPlaceholderFormat()
		));
	}

	private @NotNull Map<String, String> collectArgumentDescriptions() {
		Map<String, String> argumentDescriptions = new HashMap<>();
		Commands commands = commandsProvider.get();
		if (commands == null || commands.getCommands() == null) return argumentDescriptions;

		for (CommandDefinition definition : commands.getCommands().values()) {
			Map<String, String> arguments = definition.getArguments();
			if (arguments == null || arguments.isEmpty()) continue;
			argumentDescriptions.putAll(arguments);
		}

		return argumentDescriptions;
	}

	private void registerExceptionHandlers(@NotNull CommandManager<Actor> commandManager) {
		ExceptionMessages exceptionMessages = messagesProvider.get().getCommands() != null
				? messagesProvider.get().getCommands().getExceptions()
				: new ExceptionMessages();

		ExceptionHandlerRegistrar.register(
				commandManager,
				exceptionMessages,
				serializer,
				Actor::getAudience,
				ExceptionFormatting.builder()
						.format(Permission.class, CloudPermissionFormatters.minimal())
						.build()
		);
	}

	private static final class RecordingCommandManager extends CommandManager<Actor> {
		private final CommandManager<Actor> realManager;

		private RecordingCommandManager(@NotNull CommandManager<Actor> realManager) {
			super(ExecutionCoordinator.simpleCoordinator(), CommandRegistrationHandler.nullCommandRegistrationHandler());
			this.realManager = realManager;
		}

		@Override
		public boolean hasPermission(@NotNull Actor sender, @NotNull String permission) {
			return true;
		}

		@Override
		public @NotNull org.incendo.cloud.parser.ParserRegistry<Actor> parserRegistry() {
			return realManager.parserRegistry();
		}
	}
}
