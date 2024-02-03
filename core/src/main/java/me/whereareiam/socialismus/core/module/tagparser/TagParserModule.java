package me.whereareiam.socialismus.core.module.tagparser;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.api.model.parser.TagParser;
import me.whereareiam.socialismus.api.module.Module;
import me.whereareiam.socialismus.api.type.TagParserType;
import me.whereareiam.socialismus.core.config.module.parser.TagParserConfig;
import me.whereareiam.socialismus.core.config.setting.SettingsConfig;
import me.whereareiam.socialismus.core.util.LoggerUtil;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class TagParserModule implements Module {
	private final LoggerUtil loggerUtil;
	private final SettingsConfig settingsConfig;
	private final TagParserConfig tagParserConfig;
	private final List<TagParser> tagParsers = new ArrayList<>();
	private final Path modulePath;
	private boolean moduleStatus;

	@Inject
	public TagParserModule(LoggerUtil loggerUtil, SettingsConfig settingsConfig, TagParserConfig tagParserConfig, @Named("modulePath") Path modulePath) {
		this.loggerUtil = loggerUtil;
		this.settingsConfig = settingsConfig;
		this.tagParserConfig = tagParserConfig;
		this.modulePath = modulePath;
	}

	private void createExampleTagParser() {
		TagParser globalChat = new TagParser();

		globalChat.tag = "globalChat";
		globalChat.enabled = true;
		globalChat.type = TagParserType.HOVER;
		globalChat.content = List.of(
				" ",
				"<dark_gray> Information</dark_gray>",
				"<white>  Chat: <green>Global</green></white>  ",
				" ",
				"<gray> Global chat is a chat where you can  ",
				"<gray> talk with everyone on the server  ",
				" "
		);

		TagParser playerInformation = new TagParser();

		playerInformation.tag = "playerInformation";
		playerInformation.enabled = true;
		playerInformation.type = TagParserType.HOVER;
		playerInformation.content = List.of(
				" ",
				"<dark_gray> Information</dark_gray>",
				"<white>  Player: <green>PlaceholderAPI</green></white>  ",
				" "
		);

		TagParser messageInformation = new TagParser();

		messageInformation.tag = "messageInformation";
		messageInformation.enabled = true;
		messageInformation.type = TagParserType.HOVER;
		messageInformation.content = List.of(
				" ",
				"<dark_gray> Information</dark_gray>",
				"<white>  Message was sent at: <green>PlaceholderAPI</green></white>  ",
				" "
		);

		tagParserConfig.tagParsers.addAll(List.of(globalChat, playerInformation, messageInformation));
	}

	public List<TagParser> getTagParsers() {
		return tagParsers;
	}

	@Override
	public void initialize() {
		tagParserConfig.reload(modulePath.resolve("tagparser.yml"));
		if (tagParserConfig.tagParsers.isEmpty()) {
			loggerUtil.debug("Creating an example tag parser, because tag parser section is empty");
			createExampleTagParser();
			tagParserConfig.save(modulePath.resolve("tagparser.yml"));
		}

		tagParsers.addAll(tagParserConfig.tagParsers.stream().filter(
				tagParser -> tagParser.enabled).toList()
		);

		moduleStatus = true;
	}

	@Override
	public boolean isEnabled() {
		return moduleStatus == settingsConfig.modules.tagParser;
	}

	@Override
	public void reload() {
		loggerUtil.debug("Reloading tagparser module");

		tagParsers.clear();
		initialize();
	}
}
