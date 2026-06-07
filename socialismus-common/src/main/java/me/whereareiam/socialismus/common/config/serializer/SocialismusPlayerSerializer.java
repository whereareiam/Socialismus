package me.whereareiam.socialismus.common.config.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;

import java.io.IOException;

public final class SocialismusPlayerSerializer extends StdSerializer<SocialismusPlayer> {
	public SocialismusPlayerSerializer() {
		super(SocialismusPlayer.class);
	}

	@Override
	public void serialize(SocialismusPlayer value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		if (value == null) {
			gen.writeNull();
			return;
		}

		gen.writeStartObject();
		gen.writeStringField("uniqueId", value.getUniqueId().toString());
		gen.writeStringField("username", value.getUsername());
		writeNullableString(gen, "server", value.getServer());
		writeNullableString(gen, "location", value.getLocation());
		writeNullableObject(gen, provider, "position", value.getPosition());
		writeNullableObject(gen, provider, "eyePosition", value.getEyePosition());
		gen.writeEndObject();
	}

	private static void writeNullableString(JsonGenerator gen, String fieldName, String value) throws IOException {
		if (value == null) {
			gen.writeNullField(fieldName);
			return;
		}

		gen.writeStringField(fieldName, value);
	}

	private static void writeNullableObject(
			JsonGenerator gen,
			SerializerProvider provider,
			String fieldName,
			Object value
	) throws IOException {
		gen.writeFieldName(fieldName);
		if (value == null) {
			gen.writeNull();
			return;
		}

		provider.defaultSerializeValue(value, gen);
	}
}
