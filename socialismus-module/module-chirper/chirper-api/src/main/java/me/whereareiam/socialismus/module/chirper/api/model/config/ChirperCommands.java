package me.whereareiam.socialismus.module.chirper.api.model.config;

import lombok.Getter;
import lombok.ToString;
import me.whereareiam.socialismus.model.CommandDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Configurable command definitions exposed by Chirper.
 */
@Getter
@ToString
public class ChirperCommands {
	private final @NotNull Map<String, CommandDefinition> commands = new HashMap<>();
}
