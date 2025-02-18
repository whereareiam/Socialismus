package me.whereareiam.socialismus.api.model.config;

import lombok.Getter;
import lombok.ToString;
import me.whereareiam.socialismus.api.model.CommandEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration model for managing command configurations in the Socialismus plugin.
 * Stores a mapping of command names to their respective {@link CommandEntity} configurations.
 *
 * <p>This class is used to load and maintain command configurations from the config file.
 * Each command is identified by its name and associated with a {@link CommandEntity}
 * that defines its properties such as aliases, permissions, and cooldowns.</p>
 */
@Getter
@ToString
public class Commands {
    /**
     * Map of command names to their configuration entities.
     * Key: command name
     * Value: command configuration entity
     */
    private Map<String, CommandEntity> commands = new HashMap<>();
}