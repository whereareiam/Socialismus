package me.whereareiam.socialismus.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.commandant.model.CommandDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for command definitions.
 * Stores a mapping of command names to their respective CommandDefinition configurations.
 */
@Getter
@Setter
@ToString
public class Commands {
	/**
	 * Map of command names to their CommandDefinition configurations.
	 * Key: command name (e.g., "help", "reload")
	 * Value: CommandDefinition with aliases, permissions, descriptions, etc.
	 */
	private Map<String, CommandDefinition> commands = new HashMap<>();
}