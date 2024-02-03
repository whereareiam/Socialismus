package me.whereareiam.socialismus.core.config.module.parser;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.parser.TagParser;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.language.object.YamlSerializable;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class TagParserConfig extends YamlSerializable {

	@Comment(
			value = {
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue(" TAG PARSER DOCUMENTATION"),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue(" What is Tag Parser?"),
					@CommentValue(" It is a module that allows you to create custom tags."),
					@CommentValue(" That will parse text or hover text between the tags."),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue(" Here you can create as many tag parsers as you like."),
					@CommentValue(" tag-parsers: "),
					@CommentValue("  Here you can specify if the tag parser is enabled or not"),
					@CommentValue("  - enabled: true"),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue("    Here you can specify the tag that will be used to parse the text."),
					@CommentValue("    tag: \"example\""),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue("    Here you can specify the type of tag parser."),
					@CommentValue("    Available types:"),
					@CommentValue("      - HOVER"),
					@CommentValue("      - TEXT"),
					@CommentValue("    type: HOVER"),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue("    Here you can specify the content that will be parsed."),
					@CommentValue("    content: "),
					@CommentValue("      - \"<yellow><bold>Example</bold></yellow>\""),
					@CommentValue("      - \"<red><bold>Example</bold></red>\""),
					@CommentValue(type = CommentValue.Type.NEW_LINE),
			}
	)
	public List<TagParser> tagParsers = new ArrayList<>();
}
