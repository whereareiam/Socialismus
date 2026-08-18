package me.whereareiam.socialismus.module.essentials.feature.dialogue.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.model.CommandDefinition;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueCommands;

import java.util.List;

@Singleton
public class DialogueCommandsDefaults implements DefaultsProvider<DialogueCommands> {
	@Override
	public DialogueCommands supply(DialogueCommands dialogueCommands) {
		// Default values
		CommandDefinition msg = CommandDefinition.builder()
				.enabled(true)
				.aliases(List.of("message", "w", "whisper", "tell", "msg"))
				.permission("")
				.description("Send a private message.")
				.usage("{alias} <recipient> <message>")
				.cooldown(CommandDefinition.Cooldown.builder()
						.enabled(true)
						.duration(2)
						.group("global")
						.build()
				).build();

		CommandDefinition reply = CommandDefinition.builder()
				.enabled(true)
				.aliases(List.of("reply", "r"))
				.permission("")
				.description("Reply to the last private message.")
				.usage("{alias} <message>")
				.cooldown(CommandDefinition.Cooldown.builder()
						.enabled(true)
						.duration(2)
						.group("global")
						.build()
				).build();

		dialogueCommands.getCommands().put("message", msg);
		dialogueCommands.getCommands().put("reply", reply);

		return dialogueCommands;
	}
}
