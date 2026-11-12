package me.whereareiam.socialismus.common.config.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.whereareiam.socialismus.model.phrase.Phrases;

import java.io.IOException;
import java.util.Map;

/**
 * Writes a phrases file as it was read: texts and sections in their order.
 */
public final class PhrasesSerializer extends StdSerializer<Phrases> {
	public PhrasesSerializer() {
		super(Phrases.class);
	}

	@Override
	public void serialize(Phrases phrases, JsonGenerator generator, SerializerProvider provider) throws IOException {
		write(phrases.getEntries(), generator);
	}

	private static void write(Map<String, Phrases.Entry> entries, JsonGenerator generator) throws IOException {
		generator.writeStartObject();
		for (Map.Entry<String, Phrases.Entry> entry : entries.entrySet()) {
			generator.writeFieldName(entry.getKey());
			switch (entry.getValue()) {
				case Phrases.Entry.Text text -> generator.writeString(text.text());
				case Phrases.Entry.Section section -> write(section.entries(), generator);
			}
		}
		generator.writeEndObject();
	}
}
