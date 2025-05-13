package me.whereareiam.socialismus.command.provider;

import com.google.inject.Provider;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.PlatformInteractor;
import me.whereareiam.socialismus.api.type.PlatformType;
import me.whereareiam.socialismus.api.type.Version;
import me.whereareiam.socialismus.command.management.CommandExceptionHandler;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.exception.*;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;

public abstract class CommandManagerProvider implements Provider<CommandManager<DummyPlayer>> {
	protected final Provider<Settings> settings;
	private final PlatformInteractor interactor;
	private final CommandExceptionHandler exceptionHandler;

	private CommandManager<DummyPlayer> commandManager;

	public CommandManagerProvider(CommandExceptionHandler exceptionHandler, Provider<Settings> settings, PlatformInteractor interactor) {
		this.exceptionHandler = exceptionHandler;
		this.settings = settings;
		this.interactor = interactor;
	}

	@Override
	public CommandManager<DummyPlayer> get() {
		if (commandManager != null) return commandManager;

		commandManager = switch (PlatformType.getType()) {
			case BUKKIT, SPIGOT -> createLegacyPaperCommandManager();
			case FOLIA, PAPER -> {
				if ((interactor.getServerVersion().isAtLeast(Version.V_1_20_5) && settings.get().getMisc().isAllowBrigadierCommands()) || interactor.getServerVersion().isAtLeast(Version.V_1_20_5))
					yield createPaperCommandManager();
				yield createLegacyPaperCommandManager();
			}
			case VELOCITY -> createVelocityCommandManager();
			case UNKNOWN -> throw new IllegalStateException("Unknown platform type");
		};

		createMinecraftExceptionHandler(commandManager);

		return commandManager;
	}

	protected abstract CommandManager<DummyPlayer> createLegacyPaperCommandManager();

	protected abstract CommandManager<DummyPlayer> createPaperCommandManager();

	protected abstract CommandManager<DummyPlayer> createVelocityCommandManager();

	private void createMinecraftExceptionHandler(CommandManager<DummyPlayer> commandManager) {
		MinecraftExceptionHandler.create(DummyPlayer::getAudience)
				.handler(ArgumentParseException.class, exceptionHandler::handleParseException)
				.handler(InvalidCommandSenderException.class, exceptionHandler::handleInvalidCommandSenderException)
				.handler(NoPermissionException.class, exceptionHandler::handleNoPermissionException)
				.handler(InvalidSyntaxException.class, exceptionHandler::handleInvalidSyntaxException)
				.handler(CommandExecutionException.class, exceptionHandler::handleCommandExecutionException)
				.registerTo(commandManager);
	}
}
