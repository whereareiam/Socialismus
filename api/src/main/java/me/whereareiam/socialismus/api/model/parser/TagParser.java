package me.whereareiam.socialismus.api.model.parser;

import me.whereareiam.socialismus.api.type.TagParserType;

import java.util.ArrayList;
import java.util.List;

public class TagParser {
	public boolean enabled;
	public String tag = "default";
	public TagParserType type = TagParserType.HOVER;
	public List<String> content = new ArrayList<>();
}
