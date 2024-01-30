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
		TagParser tagParser = new TagParser();
		tagParser.tag = "chatDesc";
		tagParser.enabled = true;
		tagParser.type = TagParserType.HOVER;
		tagParser.content = List.of(
				"Chat description"
		);

		tagParserConfig.tagParsers.add(tagParser);
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
