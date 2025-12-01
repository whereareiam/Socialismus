package me.whereareiam.socialismus.command;

import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Actor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.type.PlatformType;
import me.whereareiam.socialismus.type.Version;
import org.incendo.cloud.CommandManager;

@RequiredArgsConstructor
public abstract class CommandManagerProvider implements Provider<CommandManager<Actor>> {
	protected final Provider<Settings> settings;

	private CommandManager<Actor> commandManager;

	@Override
	public CommandManager<Actor> get() {
		if (commandManager != null) return commandManager;

		commandManager = switch (PlatformType.getType()) {
			case BUKKIT, SPIGOT -> createLegacyCommandManager();
			case FOLIA, PAPER -> {
				if (Constants.SERVER_VERSION.isAtLeast(Version.V_1_20_5) && settings.get().getCommands().isUseBrigadier())
					yield createPaperCommandManager();

				yield createLegacyCommandManager();
			}
			case VELOCITY -> createVelocityCommandManager();
			case UNKNOWN -> throw new IllegalStateException("Unknown platform type");
		};

		return commandManager;
	}

	protected abstract CommandManager<Actor> createLegacyCommandManager();

	protected abstract CommandManager<Actor> createPaperCommandManager();

	protected abstract CommandManager<Actor> createVelocityCommandManager();
}
