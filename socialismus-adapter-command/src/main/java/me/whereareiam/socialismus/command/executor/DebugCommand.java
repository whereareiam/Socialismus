package me.whereareiam.socialismus.command.executor;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.Provider;
import me.whereareiam.commandant.annotation.Definition;
import me.whereareiam.keystone.Actor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.api.Serializer;
import me.whereareiam.socialismus.model.config.message.Messages;
import me.whereareiam.socialismus.output.module.ModuleService;
import me.whereareiam.socialismus.type.PlatformType;
import me.whereareiam.socialismus.type.PluginType;
import org.incendo.cloud.annotations.Command;

import java.util.stream.Collectors;

@Singleton
public class DebugCommand {
	private final Provider<Messages> messages;
	private final ModuleService moduleService;

	@Inject
	public DebugCommand(
			Provider<Messages> messages,
			ModuleService moduleService
	) {
		this.messages = messages;
		this.moduleService = moduleService;
	}

	@Definition("debug")
	@Command("socialismus debug")
	public void command(Actor actor) {
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

		actor.sendMessage(Serializer.serialize(actor, message));
	}
}