package me.whereareiam.socialismus.common.config.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import me.whereareiam.socialismus.model.position.Position;

import java.io.IOException;

public final class PositionDeserializer extends StdDeserializer<Position> {
	public PositionDeserializer() {
		super(Position.class);
	}

	@Override
	public Position deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		JsonNode node = parser.getCodec().readTree(parser);
		if (node == null || node.isNull()) return null;

		return new Position(
				number(node.get("x")),
				number(node.get("y")),
				number(node.get("z"))
		);
	}

	private static double number(JsonNode node) {
		if (node == null || node.isNull())
			throw new IllegalArgumentException("Position coordinates are required");

		return node.asDouble();
	}
}
