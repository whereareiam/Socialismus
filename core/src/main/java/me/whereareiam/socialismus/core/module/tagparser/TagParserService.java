package me.whereareiam.socialismus.core.module.tagparser;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.parser.TagParser;
import me.whereareiam.socialismus.core.util.FormatterUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.List;
import java.util.regex.Pattern;

@Singleton
public class TagParserService {
	private final FormatterUtil formatterUtil;
	private final TagParserModule tagParserModule;

	@Inject
	public TagParserService(FormatterUtil formatterUtil, TagParserModule tagParserModule) {
		this.formatterUtil = formatterUtil;
		this.tagParserModule = tagParserModule;
	}

	public Component hookTagParser(Component message) {
		String plainMessage = PlainTextComponentSerializer.plainText().serialize(message);
		List<TagParser> tagParsers = tagParserModule.getTagParsers();
		tagParsers = tagParsers.stream().filter(
				tagParser -> plainMessage.contains("<" + tagParser.tag + ">")
						&& plainMessage.contains("</" + tagParser.tag + ">")
		).toList();

		for (TagParser tagParser : tagParsers) {
			switch (tagParser.type) {
				case HOVER:
					message = formatHoverTag(message, tagParser);
					break;
				case TEXT:
					message = formatTextTag(message, tagParser);
					break;
			}
		}

		return message;
	}

	private Component formatHoverTag(Component message, TagParser tagParser) {
		String tag = tagParser.tag;
		Pattern pattern = Pattern.compile("<" + tag + ">(.*?)</" + tag + ">");

		message = message.replaceText(builder -> builder
				.match(pattern)
				.replacement(matchResult -> {
					String content = String.join("\n", tagParser.content);

					return matchResult.content(matchResult.content().replaceAll("<" + tag + ">", "")
									.replaceAll("</" + tag + ">", ""))
							.hoverEvent(formatterUtil.formatMessage(content, false));
				})
		);

		return message;
	}

	private Component formatTextTag(Component message, TagParser tagParser) {
		String tag = tagParser.tag;
		Pattern pattern = Pattern.compile("<" + tag + ">(.*?)</" + tag + ">");

		message = message.replaceText(builder -> builder
				.match(pattern)
				.replacement(matchResult -> {
					String content = matchResult.content();
					content = String.join("\n", tagParser.content) + content;

					content = content.replaceAll("<" + tag + ">", "")
							.replaceAll("</" + tag + ">", "");

					return formatterUtil.formatMessage(content, false);
				})
		);

		return message;
	}
}
