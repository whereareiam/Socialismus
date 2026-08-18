package me.whereareiam.socialismus.module.essentials.api.model.config;

import lombok.Getter;
import lombok.ToString;
import me.whereareiam.socialismus.model.CommandDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Configurable command definitions exposed by Essentials.
 */
@Getter
@ToString
public class EssentialsCommands {
	private final @NotNull Map<String, CommandDefinition> commands = new HashMap<>();
}
