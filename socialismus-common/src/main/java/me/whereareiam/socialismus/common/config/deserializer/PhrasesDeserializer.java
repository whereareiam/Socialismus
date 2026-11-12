package me.whereareiam.socialismus.common.config.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import me.whereareiam.socialismus.model.phrase.Phrases;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.SequencedMap;

/**
 * Reads a phrases file: a mapping whose values are texts or further mappings.
 */
public final class PhrasesDeserializer extends StdDeserializer<Phrases> {
	public PhrasesDeserializer() {
		super(Phrases.class);
	}

	@Override
	public Phrases deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		JsonNode node = parser.getCodec().readTree(parser);
		if (node == null || node.isNull()) return Phrases.none();
		if (!node.isObject()) return context.reportInputMismatch(Phrases.class, "Phrases are a mapping of texts and sections");

		return Phrases.of(entries(node, "", context));
	}

	@Override
	public Phrases getNullValue(DeserializationContext context) {
		return Phrases.none();
	}

	private static SequencedMap<String, Phrases.Entry> entries(JsonNode node, String path, DeserializationContext context) throws IOException {
		SequencedMap<String, Phrases.Entry> entries = new LinkedHashMap<>();
		for (var property : node.properties()) {
			String at = path + property.getKey();
			JsonNode value = property.getValue();
			if (value.isObject()) entries.put(property.getKey(), new Phrases.Entry.Section(entries(value, at + ".", context)));
			else if (value.isValueNode() && !value.isNull()) entries.put(property.getKey(), new Phrases.Entry.Text(value.asText()));
			else return context.reportInputMismatch(Phrases.class, "The phrase " + at + " is no text");
		}

		return entries;
	}
}
