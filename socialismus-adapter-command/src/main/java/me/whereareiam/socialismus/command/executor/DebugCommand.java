package me.whereareiam.socialismus.command.executor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.Constants;
import me.whereareiam.socialismus.api.Serializer;
import me.whereareiam.socialismus.api.model.CommandEntity;
import me.whereareiam.socialismus.api.model.config.message.Messages;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.PlatformInteractor;
import me.whereareiam.socialismus.api.output.command.CommandBase;
import me.whereareiam.socialismus.api.output.command.CommandCooldown;
import me.whereareiam.socialismus.api.type.PlatformType;
import me.whereareiam.socialismus.api.type.PluginType;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.Map;

@Singleton
public class DebugCommand extends CommandBase {
	private static final String COMMAND_NAME = "debug";
	private final Provider<Map<String, CommandEntity>> commands;
	private final Provider<Messages> messages;

	// Data provider
	private final PlatformInteractor platformInteractor;

	@Inject
	public DebugCommand(
			Provider<Map<String, CommandEntity>> commands,
			Provider<Messages> messages,
			PlatformInteractor platformInteractor
	) {
		super(COMMAND_NAME);
		this.commands = commands;

		this.messages = messages;
		this.platformInteractor = platformInteractor;
	}

	@Command("%command." + COMMAND_NAME)
	@CommandDescription("%description." + COMMAND_NAME)
	@CommandCooldown("%cooldown." + COMMAND_NAME)
	@Permission("%permission." + COMMAND_NAME)
	public void onCommand(DummyPlayer dummyPlayer) {
		String message = String.join("\n", messages.get().getCommands().getDebugCommand().getFormat());

		message = message.replace("{serverVersion}", platformInteractor.getServerVersion().name())
				.replace("{pluginVersion}", Constants.VERSION)
				.replace("{serverPlatform}", PlatformType.getType().name())
				.replace("{pluginPlatform}", PluginType.getType().name())
				.replace("{javaVersion}", System.getProperty("java.version"))
				.replace("{os}", System.getProperty("os.name"));

		dummyPlayer.sendMessage(Serializer.serialize(dummyPlayer, message));
	}

	@Override
	public CommandEntity getCommandEntity() {
		return commands.get().get(COMMAND_NAME);
	}
}