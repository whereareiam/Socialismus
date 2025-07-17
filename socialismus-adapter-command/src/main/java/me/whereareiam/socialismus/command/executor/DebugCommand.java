package me.whereareiam.socialismus.command.executor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.Constants;
import me.whereareiam.socialismus.api.Serializer;
import me.whereareiam.socialismus.api.model.CommandEntity;
import me.whereareiam.socialismus.api.model.config.message.Messages;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.command.CommandBase;
import me.whereareiam.socialismus.api.output.command.CommandCooldown;
import me.whereareiam.socialismus.api.output.module.ModuleService;
import me.whereareiam.socialismus.api.type.PlatformType;
import me.whereareiam.socialismus.api.type.PluginType;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class DebugCommand extends CommandBase {
	private static final String COMMAND_NAME = "debug";
	private final Provider<Map<String, CommandEntity>> commands;
	private final Provider<Messages> messages;
	private final ModuleService moduleService;

	@Inject
	public DebugCommand(
			Provider<Map<String, CommandEntity>> commands,
			Provider<Messages> messages,
			ModuleService moduleService
	) {
		super(COMMAND_NAME);
		this.commands = commands;

		this.messages = messages;
		this.moduleService = moduleService;
	}

	@Command("%command." + COMMAND_NAME)
	@CommandDescription("%description." + COMMAND_NAME)
	@CommandCooldown("%cooldown." + COMMAND_NAME)
	@Permission("%permission." + COMMAND_NAME)
	public void onCommand(DummyPlayer dummyPlayer) {
		String message = String.join("\n", messages.get().getCommands().getDebugCommand().getFormat());
		
		String moduleFormat = messages.get().getCommands().getDebugCommand().getModuleFormat();
		String modules = moduleService.getModules().stream()
				.map(module -> moduleFormat
						.replace("{name}", module.getName())
						.replace("{version}", module.getVersion())
						.replace("{authors}", String.join(", ", module.getAuthors()))
				).collect(Collectors.joining("\n"));

		message = message.replace("{serverVersion}", Constants.SERVER_VERSION.name())
				.replace("{pluginVersion}", Constants.VERSION)
				.replace("{serverPlatform}", PlatformType.getType().name())
				.replace("{pluginPlatform}", PluginType.getType().name())
				.replace("{javaVersion}", System.getProperty("java.version"))
				.replace("{os}", System.getProperty("os.name"))
				.replace("{modules}", modules);

		dummyPlayer.sendMessage(Serializer.serialize(dummyPlayer, message));
	}

	@Override
	public CommandEntity getCommandEntity() {
		return commands.get().get(COMMAND_NAME);
	}
}