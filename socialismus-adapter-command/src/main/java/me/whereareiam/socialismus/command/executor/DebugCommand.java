package me.whereareiam.socialismus.command.executor;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.Provider;
import me.whereareiam.commandant.annotation.Definition;
import me.whereareiam.keystone.Actor;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.model.config.message.Messages;
import me.whereareiam.socialismus.module.ModuleService;
import me.whereareiam.socialismus.type.PlatformType;
import me.whereareiam.socialismus.type.PluginType;
import net.kyori.adventure.text.Component;
import org.incendo.cloud.annotations.Command;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class DebugCommand {
	private final Provider<Messages> messages;
	private final ModuleService moduleService;

	@Inject
	public DebugCommand(
			@NotNull Provider<Messages> messages,
			@NotNull ModuleService moduleService
	) {
		this.messages = messages;
		this.moduleService = moduleService;
	}

	@Definition("debug")
	@Command("socialismus debug")
	public void command(@NotNull Actor actor) {
		String message = String.join("\n", messages.get().getCommands().getDebugCommand().getFormat());
		
		String moduleFormat = messages.get().getCommands().getDebugCommand().getModuleFormat();
		String modules = moduleService.getModules().stream()
				.map(module -> moduleFormat
						.replace("{name}", module.getName())
						.replace("{version}", module.getVersion())
						.replace("{authors}", String.join(", ", module.getAuthors()))
				).collect(Collectors.joining("\n"));

		Component component = Serializer.serialize(SerializerContent.builder()
				.receiver(actor)
				.message(message)
				.placeholders(Map.of(
						"{serverVersion}", Constants.SERVER_VERSION.name(),
						"{pluginVersion}", Constants.VERSION,
						"{serverPlatform}", PlatformType.getType().name(),
						"{pluginPlatform}", PluginType.getType().name(),
						"{javaVersion}", System.getProperty("java.version"),
						"{os}", System.getProperty("os.name"),
						"{modules}", modules
				))
				.build());

		actor.sendMessage(component);
	}
}