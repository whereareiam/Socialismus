package me.whereareiam.socialismus.output.command;

import lombok.Getter;
import me.whereareiam.socialismus.model.CommandEntity;

import java.util.Map;

/**
 * Abstract base class for command implementations in the Socialismus plugin.
 * Provides common functionality for command handling and translation management.
 *
 * <p>Commands extending this class must implement the {@link #getCommandEntity()}
 * method to provide command-specific details.</p>
 */
@Getter
public abstract class CommandBase {
	/**
	 * The name of the command.
	 */
	protected final String commandName;

	/**
	 * Constructs a new command with the specified name.
	 *
	 * @param commandName the name of the command
	 */
	protected CommandBase(String commandName) {
		this.commandName = commandName;
	}

	/**
	 * Gets the command entity containing command details.
	 *
	 * @return the command entity with configuration and metadata
	 */
	public abstract CommandEntity getCommandEntity();

	/**
	 * Generates a map of translation keys and their values for the command.
	 * Includes translations for command name, permission, description, and usage.
	 *
	 * @return a map of translation keys and their corresponding values
	 */
	public Map<String, String> getTranslations() {
		CommandEntity commandEntity = getCommandEntity();

		return Map.of(
				"command." + commandEntity.getAliases().get(0) + ".name", commandEntity.getUsage().replace("{alias}", String.join("|", commandEntity.getAliases())),
				"command." + commandEntity.getAliases().get(0) + ".permission", commandEntity.getPermission(),
				"command." + commandEntity.getAliases().get(0) + ".description", commandEntity.getDescription(),
				"command." + commandEntity.getAliases().get(0) + ".usage", commandEntity.getUsage()
		);
	}
}