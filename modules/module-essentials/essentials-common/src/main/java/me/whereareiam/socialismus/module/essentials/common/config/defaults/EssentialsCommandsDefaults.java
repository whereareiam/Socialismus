package me.whereareiam.socialismus.module.essentials.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.model.CommandDefinition;
import me.whereareiam.socialismus.module.essentials.api.model.config.EssentialsCommands;

import java.util.List;

@Singleton
public class EssentialsCommandsDefaults implements DefaultsProvider<EssentialsCommands> {
	@Override
	public EssentialsCommands supply(EssentialsCommands config) {
		// Default values
		CommandDefinition features = CommandDefinition.builder()
				.enabled(true)
				.aliases(List.of("features"))
				.permission("socialismus.admin")
				.description("Shows enabled features.")
				.usage("{command} {alias}")
				.cooldown(CommandDefinition.Cooldown.builder()
						.enabled(true)
						.duration(2)
						.group("global")
						.build()
				).build();

		config.getCommands().put("features", features);

		return config;
	}
}
