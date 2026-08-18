package me.whereareiam.socialismus.module.chirper.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.model.CommandDefinition;
import me.whereareiam.socialismus.module.chirper.api.model.config.ChirperCommands;

import java.util.List;
import java.util.Map;

@Singleton
public class ChirperCommandsDefaults implements DefaultsProvider<ChirperCommands> {
	@Override
	public ChirperCommands supply(ChirperCommands config) {
		CommandDefinition announce = CommandDefinition.builder()
				.enabled(true)
				.aliases(List.of("announce", "broadcast", "chirp"))
				.permission("chirper.admin")
				.description("Announce command")
				.usage("{command} {alias} <id> [simplified]")
				.cooldown(CommandDefinition.Cooldown.builder()
						.enabled(true)
						.duration(2)
						.group("global")
						.build()
				)
				.arguments(Map.of(
						"id", "ID",
						"simplified", "Simplify"
				))
				.build();

		config.getCommands().put("announce", announce);

		return config;
	}
}
