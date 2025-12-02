package me.whereareiam.socialismus.service;

import me.whereareiam.commandant.model.CommandDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Service interface for managing commands and their translations in the Socialismus plugin.
 * Provides functionality for registering commands, managing translations, and retrieving
 * command-related information.
 */
@SuppressWarnings("unused")
public interface CommandService {
	/**
	 * Gets the total number of registered commands.
	 *
	 * @return the number of registered commands
	 */
	int getCommandCount();

	/**
	 * Registers a command with its definition from external API users (e.g., modules).
	 * The command class should contain Cloud annotations (@Command, @Definition, etc.)
	 * that define the command structure. The instance will be created through dependency injection.
	 * <p>
	 * The definition key must match the value in the {@link @Definition} annotation on the
	 * command class or method.
	 *
	 * @param key the key that matches the @Definition annotation value
	 * @param definition the CommandDefinition for this command
	 * @param commandClass the class containing Cloud annotations to register as a command
	 */
	void registerCommand(@NotNull String key, @NotNull CommandDefinition definition, @NotNull Class<?> commandClass);

	/**
	 * Registers multiple commands with their definitions from external API users (e.g., modules).
	 * Each command class should contain Cloud annotations (@Command, @Definition, etc.)
	 * that define the command structure. Instances will be created through dependency injection.
	 * <p>
	 * The definition keys will be extracted from the {@link @Definition} annotations on the
	 * command classes or methods. If the annotation is present on both, the method-level value takes precedence.
	 *
	 * @param definitions map of definition keys to CommandDefinition objects
	 * @param commandClasses classes containing Cloud annotations to register as commands
	 */
	void registerCommands(@NotNull Map<String, CommandDefinition> definitions, @NotNull Class<?>... commandClasses);

	/**
	 * Registers a pre-instantiated command object with its definition.
	 * Use this when your module has its own injector with custom bindings.
	 * The command object should contain Cloud annotations (@Command, @Definition, etc.).
	 *
	 * @param key the key that matches the @Definition annotation value
	 * @param definition the CommandDefinition for this command
	 * @param commandInstance the pre-instantiated command object
	 */
	void registerCommandInstance(@NotNull String key, @NotNull CommandDefinition definition, @NotNull Object commandInstance);

	/**
	 * Registers multiple pre-instantiated command objects with their definitions.
	 * Use this when your module has its own injector with custom bindings.
	 *
	 * @param definitions map of definition keys to CommandDefinition objects
	 * @param commandInstances pre-instantiated command objects
	 */
	void registerCommandInstances(@NotNull Map<String, CommandDefinition> definitions, @NotNull Object... commandInstances);
}