package me.whereareiam.socialismus.common.config.template;

import com.google.inject.Singleton;
import me.whereareiam.configura.TemplateProvider;
import me.whereareiam.socialismus.model.CommandEntity;
import me.whereareiam.socialismus.model.config.Commands;

import java.util.List;

@Singleton
public class CommandsTemplate implements TemplateProvider<Commands> {
	@Override
	public Commands supply(Commands commands) {
		// Default values
		CommandEntity main = CommandEntity.builder()
				.enabled(true)
				.aliases(List.of("socialismus", "social"))
				.permission("")
				.description("Main command")
				.usage("{alias}")
				.cooldown(CommandEntity.Cooldown.builder()
						.enabled(true)
						.duration(2)
						.group("global")
						.build()
				).build();

		CommandEntity help = CommandEntity.builder()
				.enabled(true)
				.aliases(List.of("help"))
				.permission("")
				.description("Help command")
				.usage("{command} {alias} [page]")
				.cooldown(CommandEntity.Cooldown.builder()
						.enabled(true)
						.duration(2)
						.group("global")
						.build()
				).build();

		CommandEntity debug = CommandEntity.builder()
				.enabled(true)
				.aliases(List.of("debug"))
				.permission("socialismus.admin")
				.description("Debug command")
				.usage("{command} {alias}")
				.cooldown(CommandEntity.Cooldown.builder()
						.enabled(true)
						.duration(2)
						.group("global")
						.build()
				).build();

		CommandEntity reload = CommandEntity.builder()
				.enabled(true)
				.aliases(List.of("reload"))
				.permission("socialismus.admin")
				.description("Reload command")
				.usage("{command} {alias}")
				.cooldown(CommandEntity.Cooldown.builder()
						.enabled(true)
						.duration(2)
						.group("global")
						.build()
				).build();

		CommandEntity clear = CommandEntity.builder()
				.enabled(true)
				.aliases(List.of("clear", "clearchat"))
				.permission("socialismus.admin")
				.description("Clear command")
				.usage("{command} {alias} [context]")
				.cooldown(CommandEntity.Cooldown.builder()
						.enabled(true)
						.duration(2)
						.group("global")
						.build()
				).build();

		commands.getCommands().put("main", main);
		commands.getCommands().put("help", help);
		commands.getCommands().put("debug", debug);
		commands.getCommands().put("reload", reload);
		commands.getCommands().put("clear", clear);

		return commands;
	}
}
