package me.whereareiam.socialismus.common.config.template;

import com.google.inject.Singleton;
import me.whereareiam.commandant.model.CommandDefinition;
import me.whereareiam.configura.TemplateProvider;
import me.whereareiam.socialismus.model.config.Commands;

import java.util.List;
import java.util.Map;

@Singleton
public class CommandsTemplate implements TemplateProvider<Commands> {
	@Override
	public Commands supply(Commands commands) {
		// Default values
		CommandDefinition main = CommandDefinition.builder()
				.enabled(true)
				.aliases(List.of("socialismus", "social"))
				.permission("")
				.description("Main command")
				.usage("{alias}")
				.cooldown(CommandDefinition.Cooldown.builder()
						.enabled(true)
						.duration(2)
						.group("global")
						.build()
				).build();

		CommandDefinition help = CommandDefinition.builder()
				.enabled(true)
				.aliases(List.of("help"))
				.permission("")
				.description("Help command")
				.usage("{command} {alias} [page]")
				.cooldown(CommandDefinition.Cooldown.builder()
						.enabled(true)
						.duration(2)
						.group("global")
						.build()
				)
				.arguments(Map.of(
						"page", "Page"
				))
				.build();

		CommandDefinition debug = CommandDefinition.builder()
				.enabled(true)
				.aliases(List.of("debug"))
				.permission("socialismus.admin")
				.description("Debug command")
				.usage("{command} {alias}")
				.cooldown(CommandDefinition.Cooldown.builder()
						.enabled(true)
						.duration(2)
						.group("global")
						.build()
				).build();

		CommandDefinition reload = CommandDefinition.builder()
				.enabled(true)
				.aliases(List.of("reload"))
				.permission("socialismus.admin")
				.description("Reload command")
				.usage("{command} {alias}")
				.cooldown(CommandDefinition.Cooldown.builder()
						.enabled(true)
						.duration(2)
						.group("global")
						.build()
				).build();

		CommandDefinition clear = CommandDefinition.builder()
				.enabled(true)
				.aliases(List.of("clear", "clearchat"))
				.permission("socialismus.admin")
				.description("Clear command")
				.usage("{command} {alias} [context]")
				.cooldown(CommandDefinition.Cooldown.builder()
						.enabled(true)
						.duration(2)
						.group("global")
						.build()
				)
				.arguments(Map.of(
						"context", "Context"
				))
				.build();

		commands.getCommands().put("main", main);
		commands.getCommands().put("help", help);
		commands.getCommands().put("debug", debug);
		commands.getCommands().put("reload", reload);
		commands.getCommands().put("clear", clear);

		return commands;
	}
}
