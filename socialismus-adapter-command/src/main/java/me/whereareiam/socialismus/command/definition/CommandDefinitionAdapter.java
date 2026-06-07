package me.whereareiam.socialismus.command.definition;

import me.whereareiam.commandant.adapter.DefinitionAdapter;
import me.whereareiam.socialismus.model.CommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class CommandDefinitionAdapter implements DefinitionAdapter<CommandDefinition> {
	@Override
	public boolean isEnabled(@NotNull CommandDefinition definition) {
		return definition.isEnabled();
	}

	@Override
	public @Nullable List<String> getAliases(@NotNull CommandDefinition definition) {
		return definition.getAliases();
	}

	@Override
	public @Nullable String getPermission(@NotNull CommandDefinition definition) {
		return definition.getPermission();
	}

	@Override
	public @Nullable String getDescription(@NotNull CommandDefinition definition) {
		return definition.getDescription();
	}

	@Override
	public @Nullable String getUsage(@NotNull CommandDefinition definition) {
		return definition.getUsage();
	}

	@Override
	public @Nullable Cooldown getCooldown(@NotNull CommandDefinition definition) {
		CommandDefinition.Cooldown cooldown = definition.getCooldown();
		if (cooldown == null) return null;

		return new CooldownAdapter(cooldown);
	}

	@Override
	public @Nullable Map<String, String> getArguments(@NotNull CommandDefinition definition) {
		return definition.getArguments();
	}

	private static final class CooldownAdapter implements Cooldown {
		private final CommandDefinition.Cooldown cooldown;

		private CooldownAdapter(@NotNull CommandDefinition.Cooldown cooldown) {
			this.cooldown = cooldown;
		}

		@Override
		public boolean isEnabled() {
			return cooldown.isEnabled();
		}

		@Override
		public int getDuration() {
			return cooldown.getDuration();
		}

		@Override
		public @NotNull String getGroup() {
			return cooldown.getGroup();
		}
	}
}
