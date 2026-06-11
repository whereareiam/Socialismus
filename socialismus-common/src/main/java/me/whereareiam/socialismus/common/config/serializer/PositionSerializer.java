package me.whereareiam.socialismus.common.config.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.whereareiam.socialismus.model.position.Position;

import java.io.IOException;

public final class PositionSerializer extends StdSerializer<Position> {
	public PositionSerializer() {
		super(Position.class);
	}

	@Override
	public void serialize(Position value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		if (value == null) {
			gen.writeNull();
			return;
		}

		gen.writeStartObject();
		gen.writeNumberField("x", value.getX());
		gen.writeNumberField("y", value.getY());
		gen.writeNumberField("z", value.getZ());
		gen.writeEndObject();
	}
}
