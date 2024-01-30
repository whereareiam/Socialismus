package me.whereareiam.socialismus.core.config.module.parser;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.parser.TagParser;
import net.elytrium.serializer.language.object.YamlSerializable;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class TagParserConfig extends YamlSerializable {
	public List<TagParser> tagParsers = new ArrayList<>();
}
