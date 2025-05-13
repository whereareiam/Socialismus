package me.whereareiam.socialismus.command.management;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.CommandEntity;
import me.whereareiam.socialismus.api.output.command.CommandService;
import org.incendo.cloud.annotations.string.PatternReplacingStringProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class CommandTranslator {
	private static final Pattern COMMAND_PATTERN = Pattern.compile("%command\\.(.*)");
	private static final Pattern PERMISSION_PATTERN = Pattern.compile("%permission\\.(.*)");
	private static final Pattern DESCRIPTION_PATTERN = Pattern.compile("%description\\.(.*)");
	private static final Pattern USAGE_PATTERN = Pattern.compile("%usage\\.(.*)");

	private final Provider<Map<String, CommandEntity>> commands;
	private final CommandService commandService;

	@Inject
	public CommandTranslator(Provider<Map<String, CommandEntity>> commands, CommandService commandService) {
		this.commands = commands;
		this.commandService = commandService;
	}

	public PatternReplacingStringProcessor getProcessor() {
		Map<Pattern, Function<CommandEntity, String>> patternFunctionMap = new HashMap<>();

		patternFunctionMap.put(COMMAND_PATTERN, command -> getTranslation("command." + command.getAliases().get(0) + ".name"));
		patternFunctionMap.put(PERMISSION_PATTERN, command -> getTranslation("command." + command.getAliases().get(0) + ".permission"));
		patternFunctionMap.put(DESCRIPTION_PATTERN, command -> getTranslation("command." + command.getAliases().get(0) + ".description"));
		patternFunctionMap.put(USAGE_PATTERN, command -> getTranslation("command." + command.getAliases().get(0) + ".usage"));

		Function<MatchResult, String> replacementFunction = matchResult -> {
			String input = matchResult.group(0);

			for (Map.Entry<Pattern, Function<CommandEntity, String>> entry : patternFunctionMap.entrySet()) {
				Matcher matcher = entry.getKey().matcher(input);
				if (matcher.find()) {
					String commandName = matcher.group(1);
					CommandEntity commandEntity = commands.get().get(commandName);

					if (commandEntity != null) {
						String result = entry.getValue().apply(commandEntity);
						if (result == null || result.isEmpty()) return "";

						return result.replace("{command}", String.join("|", commands.get().get("main").getAliases()));
					}
				}
			}

			return input;
		};

		return new PatternReplacingStringProcessor(Pattern.compile(".*"), replacementFunction);
	}

	private String getTranslation(String key) {
		return commandService.getTranslation(key);
	}
}
