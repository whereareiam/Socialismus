package me.whereareiam.socialismus.common.config.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.player.SyncedSocialismusPlayer;
import me.whereareiam.socialismus.model.position.Position;

import java.io.IOException;
import java.util.UUID;

public final class SocialismusPlayerDeserializer extends StdDeserializer<SocialismusPlayer> {
	public SocialismusPlayerDeserializer() {
		super(SocialismusPlayer.class);
	}

	@Override
	public SocialismusPlayer deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		JsonNode node = parser.getCodec().readTree(parser);
		if (node == null || node.isNull()) return null;

		if (node.isTextual()) {
			String value = node.asText().trim();
			if (value.isEmpty()) return null;
			return new SyncedSocialismusPlayer(UUID.fromString(value), value);
		}

		JsonNode uniqueIdNode = node.get("uniqueId");
		if (uniqueIdNode == null || uniqueIdNode.isNull())
			throw new IllegalArgumentException("SocialismusPlayer uniqueId is required");

		UUID uniqueId = UUID.fromString(uniqueIdNode.asText().trim());
		String username = text(node.get("username"), "");
		String server = text(node.get("server"), null);
		String location = text(node.get("location"), null);
		Position position = readPosition(parser, node.get("position"));
		Position eyePosition = readPosition(parser, node.get("eyePosition"));

		return new SyncedSocialismusPlayer(uniqueId, username, server, location, position, eyePosition);
	}

	private static String text(JsonNode node, String fallback) {
		if (node == null || node.isNull()) return fallback;
		String value = node.asText();
		return value == null || value.isBlank() ? fallback : value;
	}

	private static Position readPosition(JsonParser parser, JsonNode node) throws IOException {
		if (node == null || node.isNull()) return null;
		return parser.getCodec().treeToValue(node, Position.class);
	}
}
