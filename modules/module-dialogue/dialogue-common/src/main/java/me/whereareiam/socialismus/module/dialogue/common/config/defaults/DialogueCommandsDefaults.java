package me.whereareiam.socialismus.module.dialogue.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.model.CommandDefinition;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueCommands;

import java.util.List;

@Singleton
public class DialogueCommandsDefaults implements DefaultsProvider<DialogueCommands> {
	@Override
	public DialogueCommands supply(DialogueCommands dialogueCommands) {
		// Default values
		CommandDefinition msg = CommandDefinition.builder()
				.enabled(true)
				.aliases(List.of("message", "msg", "w", "tell", "whisper"))
				.permission("")
				.description("Send a private message.")
				.usage("{alias} <recipient> [message]")
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

		CommandDefinition toggle = CommandDefinition.builder()
				.enabled(true)
				.aliases(List.of("msgtoggle", "pmtoggle"))
				.permission("")
				.description("Turn private messages off or back on.")
				.usage("{alias}")
				.cooldown(CommandDefinition.Cooldown.builder()
						.enabled(true)
						.duration(2)
						.group("global")
						.build()
				).build();

		CommandDefinition ignore = CommandDefinition.builder()
				.enabled(true)
				.aliases(List.of("ignore"))
				.permission("")
				.description("Ignore a player, stop ignoring them, or list whom you ignore.")
				.usage("{alias} [player]")
				.cooldown(CommandDefinition.Cooldown.builder()
						.enabled(true)
						.duration(2)
						.group("global")
						.build()
				).build();

		CommandDefinition spy = CommandDefinition.builder()
				.enabled(true)
				.aliases(List.of("socialspy", "spy"))
				.permission("socialismus.dialogue.spy")
				.description("See the private messages of others, or stop.")
				.usage("{alias}")
				.cooldown(CommandDefinition.Cooldown.builder()
						.enabled(false)
						.duration(0)
						.group("global")
						.build()
				).build();

		dialogueCommands.getCommands().put("message", msg);
		dialogueCommands.getCommands().put("spy", spy);
		dialogueCommands.getCommands().put("toggle", toggle);
		dialogueCommands.getCommands().put("ignore", ignore);
		dialogueCommands.getCommands().put("reply", reply);

		return dialogueCommands;
	}
}
