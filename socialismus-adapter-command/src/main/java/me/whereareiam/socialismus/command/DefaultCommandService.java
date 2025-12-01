package me.whereareiam.socialismus.command;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.commandant.Commandant;
import me.whereareiam.commandant.model.CommandDefinition;
import me.whereareiam.commandant.model.message.ExceptionMessages;
import me.whereareiam.commandant.registration.CommandRegistrar;
import me.whereareiam.socialismus.api.model.config.Commands;
import me.whereareiam.socialismus.api.model.config.message.Messages;
import me.whereareiam.socialismus.api.output.command.CommandService;
import me.whereareiam.socialismus.command.executor.*;
import me.whereareiam.socialismus.command.suggestion.CrossPlayerProvider;
import me.whereareiam.socialismus.command.suggestion.PlayerSuggestionProvider;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;
import org.jetbrains.annotations.NotNull;

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

	public void initialize() {
		CommandManager<Actor> commandManager = commandManagerProvider.get();
		Function<String, CommandDefinition> definitionLookup = this::lookupDefinition;

		// Register suggestion providers first using the real command manager
		registerSuggestionProviders(commandManager);

		CommandRegistrar<Actor> registrar = Commandant.createAnnotationRegistrar(
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