package me.whereareiam.socialismus.module.dialogue.api.model.config;

import lombok.Getter;
import lombok.ToString;
import me.whereareiam.socialismus.model.CommandDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * The commands of Dialogue, {@code commands} in its working directory: {@code message} and {@code reply}.
 */
@Getter
@ToString
public class DialogueCommands {
	/**
	 * The definition of each command, by its name.
	 */
	private final @NotNull Map<String, CommandDefinition> commands = new HashMap<>();
}
