package me.whereareiam.socialismus.command;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.commandant.Commandant;
import me.whereareiam.commandant.model.CommandDefinition;
import me.whereareiam.commandant.model.message.ExceptionMessages;
import me.whereareiam.commandant.registration.CommandRegistrar;
import me.whereareiam.keystone.Actor;
import me.whereareiam.keystone.Player;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.command.executor.*;
import me.whereareiam.socialismus.command.suggestion.CrossPlayerProvider;
import me.whereareiam.socialismus.command.suggestion.PlayerSuggestionProvider;
import me.whereareiam.socialismus.model.config.Commands;
import me.whereareiam.socialismus.model.config.message.Messages;
import me.whereareiam.socialismus.output.command.CommandService;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

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
	private CommandRegistrar<Actor> registrar;

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
		if (registrar == null) throw new IllegalStateException("CommandService has not been initialized yet. Commands can only be registered after plugin initialization.");

		// Register the definition
		registeredDefinitions.put(key, definition);

		// Instantiate and register the command class
		Object instance = injector.getInstance(commandClass);
		registrar.register(instance);
	}

	@Override
	public void registerCommands(@NotNull Map<String, CommandDefinition> definitions, @NotNull Class<?>... commandClasses) {
		if (registrar == null) throw new IllegalStateException("CommandService has not been initialized yet. Commands can only be registered after plugin initialization.");

		// Register all definitions first
		registeredDefinitions.putAll(definitions);

		// Instantiate command classes through dependency injection
		Object[] instances = new Object[commandClasses.length];
		for (int i = 0; i < commandClasses.length; i++)
			instances[i] = injector.getInstance(commandClasses[i]);

		registrar.register(instances);
	}

	@Override
	public int getCommandCount() {
		return commandManagerProvider.get().commands().size();
	}

	public void initialize() {
		CommandManager<Actor> commandManager = commandManagerProvider.get();
		Function<String, CommandDefinition> definitionLookup = this::lookupDefinition;

		// Register suggestion providers first using the real command manager
		registerSuggestionProviders(commandManager);

		this.registrar = Commandant.createAnnotationRegistrar(
				commandManager,
				this::resolveCooldownKey,
				Actor.class,
				definitionLookup
		);

		registrar.setRootCommand(resolveRootCommand(definitionLookup));
		registerCommands(registrar);
		registerExceptionHandlers(commandManager);
	}

	private CommandDefinition lookupDefinition(@NotNull String key) {
		// First check registered definitions from external API users
		CommandDefinition registered = registeredDefinitions.get(key);
		if (registered != null) return registered;
		
		// Fall back to config file definitions
		Commands commands = commandsProvider.get();
		return commands.getCommands().get(key);
	}

	private @NotNull UUID resolveCooldownKey(@NotNull Actor actor) {
		if (actor instanceof Player player) {
			return player.getUniqueId();
		}
		return UUID.nameUUIDFromBytes(actor.getClass().getName().getBytes());
	}

	private @NotNull String resolveRootCommand(@NotNull Function<String, CommandDefinition> definitionLookup) {
		CommandDefinition definition = definitionLookup.apply("main");
		if (definition == null || definition.getAliases() == null || definition.getAliases().isEmpty())
			return "intercept";

		return definition.getAliases().getFirst();
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

	private void registerCommands(@NotNull CommandRegistrar<Actor> registrar) {
		registrar.register(
				injector.getInstance(MainCommand.class),
				injector.getInstance(HelpCommand.class),
				injector.getInstance(DebugCommand.class),
				injector.getInstance(ReloadCommand.class),
				injector.getInstance(ClearCommand.class)
		);
	}

	private void registerExceptionHandlers(@NotNull CommandManager<Actor> commandManager) {
		ExceptionMessages exceptionMessages = messagesProvider.get().getCommands() != null
				? messagesProvider.get().getCommands().getExceptions()
				: new ExceptionMessages();

		Commandant.registerExceptionHandler(
				exceptionMessages,
				serializer,
				commandManager,
				Actor::getAudience
		);
	}
}