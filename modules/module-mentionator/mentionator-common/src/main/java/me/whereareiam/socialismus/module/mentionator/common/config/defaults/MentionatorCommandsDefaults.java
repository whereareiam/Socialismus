package me.whereareiam.socialismus.module.mentionator.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.model.CommandDefinition;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorCommands;

import java.util.List;
import java.util.Map;

@Singleton
public class MentionatorCommandsDefaults implements DefaultsProvider<MentionatorCommands> {
	@Override
	public MentionatorCommands supply(MentionatorCommands commands) {
		CommandDefinition mentions = CommandDefinition.builder()
				.enabled(true)
				.aliases(List.of("mentions", "tags"))
				.permission("")
				.description("Choose which tags notify you, and see the tags you may use.")
				.usage("{command} {alias} [mode]")
				.cooldown(CommandDefinition.Cooldown.builder()
						.enabled(true)
						.duration(2)
						.group("global")
						.build())
				.arguments(Map.of("mode", "Mode"))
				.build();

		commands.getCommands().put("mentions", mentions);

		return commands;
	}
}
