package me.whereareiam.socialismus.module.essentials.feature.dialogue.config;

import lombok.Getter;
import lombok.Setter;
import me.whereareiam.socialismus.model.CommandDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class DialogueCommands {
	private @NotNull Map<String, CommandDefinition> commands = new HashMap<>();
}
