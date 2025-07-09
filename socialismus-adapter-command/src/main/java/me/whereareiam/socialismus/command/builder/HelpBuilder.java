package me.whereareiam.socialismus.command.builder;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.model.config.message.CommandMessages;
import me.whereareiam.socialismus.api.model.config.message.Messages;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import org.incendo.cloud.Command;
import org.incendo.cloud.component.CommandComponent;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class HelpBuilder {
	private final Provider<Settings> settings;
	private final Provider<Messages> messages;
	private final PaginationBuilder paginationBuilder;

	@Inject
	public HelpBuilder(Provider<Settings> settings, Provider<Messages> messages, PaginationBuilder paginationBuilder) {
		this.settings = settings;
		this.messages = messages;
		this.paginationBuilder = paginationBuilder;
	}

	public String buildHelpMessage(Collection<Command<DummyPlayer>> allowedCommands, int page) {
		String message = String.join("\n", messages.get().getCommands().getHelpCommand().getFormat());

		if (message.contains("{commands}")) message = buildCommandList(allowedCommands, message, page);
		if (message.contains("{pagination}")) message = paginationBuilder.build(message, allowedCommands.size(), page);

		return message;
	}

	private String buildCommandList(Collection<Command<DummyPlayer>> commands, String message, int page) {
		if (commands.isEmpty())
			return message.replace("{commands}", messages.get().getCommands().getHelpCommand().getNoCommands());

		long skip = (long) (page - 1) * settings.get().getMisc().getCommandsPerPage();

		List<String> commandDescriptions = commands.stream().skip(skip).limit(settings.get().getMisc().getCommandsPerPage()).map(command -> {
			String commandFormat = messages.get().getCommands().getHelpCommand().getCommandFormat();
			return commandFormat.replace("{command}", command.rootComponent().name())
					.replace("{arguments}", formatCommandArguments(command.rootComponent().name(), command.nonFlagArguments()))
					.replace("{description}", command.commandDescription().description().textDescription());
		}).collect(Collectors.toList());

		String commandsString = String.join("\n", commandDescriptions);
		return message.replace("{commands}", commandsString);
	}

	private String formatCommandArguments(String commandName, List<? extends CommandComponent<?>> arguments) {
		if (arguments.isEmpty()) return "";

		CommandMessages commandMessages = messages.get().getCommands();

		CommandMessages.Format messageFormat = messages.get().getCommands().getFormat();
		return arguments.parallelStream()
				.skip(1)
				.map(argument -> switch (argument.type()) {
					case REQUIRED_VARIABLE ->
							messageFormat.getArgument().replace("{argument}", commandMessages.getArguments().get(argument.name()) != null ? commandMessages.getArguments().get(argument.name()) : argument.name());
					case OPTIONAL_VARIABLE ->
							messageFormat.getOptionalArgument().replace("{argument}", commandMessages.getArguments().get(argument.name()) != null ? commandMessages.getArguments().get(argument.name()) : argument.name());
					default ->
							commandMessages.getArguments().get(argument.name()) != null ? commandMessages.getArguments().get(argument.name()) : argument.name();
				})
				.collect(Collectors.collectingAndThen(
						Collectors.joining(" "),
						formattedArguments -> formattedArguments.isEmpty() ? "" : " " + formattedArguments
				));
	}
}