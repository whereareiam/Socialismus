package me.whereareiam.socialismus.api.output.command;

import java.util.Map;

/**
 * Service interface for managing commands and their translations in the Socialismus plugin.
 * Provides functionality for registering commands, managing translations, and retrieving
 * command-related information.
 */
public interface CommandService {
    /**
     * Registers a single command in the command system.
     *
     * @param command the command to register
     */
    void registerCommand(CommandBase command);

    /**
     * Registers all configured commands in the command system.
     * This method should be called during plugin initialization.
     */
    void registerCommands();

    /**
     * Registers a translation key-value pair for command messages.
     *
     * @param key the translation key
     * @param value the translated message
     */
    void registerTranslation(String key, String value);

    /**
     * Gets the total number of registered commands.
     *
     * @return the number of registered commands
     */
    int getCommandCount();

    /**
     * Retrieves a translation for the specified key.
     *
     * @param key the translation key to look up
     * @return the translated message
     */
    String getTranslation(String key);

    /**
     * Gets all registered translations.
     *
     * @return a map of translation keys to their translated messages
     */
    Map<String, String> getTranslations();
}