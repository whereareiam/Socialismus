package me.whereareiam.socialismus.adapter.config.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.chat.ChatTrigger;
import me.whereareiam.socialismus.api.model.chat.trigger.CommandChatTrigger;
import me.whereareiam.socialismus.api.model.chat.trigger.RegexChatTrigger;
import me.whereareiam.socialismus.api.model.chat.trigger.SymbolChatTrigger;
import me.whereareiam.socialismus.api.type.chat.TriggerType;

import java.io.IOException;

@Singleton
public class ChatTriggerDeserializer extends JsonDeserializer<ChatTrigger> {
	@Override
	public ChatTrigger deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
		ObjectCodec codec = parser.getCodec();
		JsonNode root = codec.readTree(parser);

		TriggerType type = resolveType(root);
		return switch (type) {
			case SYMBOL -> buildSymbol(root);
			case REGEX -> buildRegex(root);
			case COMMAND -> buildCommand(root);
		};
	}

	private TriggerType resolveType(JsonNode node) {
		if (node.has("type") && node.get("type").isTextual()) {
			try {
				return TriggerType.valueOf(node.get("type").asText());
			} catch (IllegalArgumentException ignored) {
			}
		}

		return TriggerType.SYMBOL;
	}

	private SymbolChatTrigger buildSymbol(JsonNode node) {
		String symbol = node.get("symbol").asText();
		boolean strip = !node.has("strip") || node.get("strip").asBoolean();
		Integer radius = node.has("radius") && node.get("radius").canConvertToInt() ? node.get("radius").asInt() : null;

		return SymbolChatTrigger.builder()
				.type(TriggerType.SYMBOL)
				.symbol(symbol)
				.strip(strip)
				.radius(radius)
				.build();
	}

	private RegexChatTrigger buildRegex(JsonNode node) {
		String regex = node.get("pattern").asText();
		boolean strip = !node.has("strip") || node.get("strip").asBoolean();
		Integer radius = node.has("radius") && node.get("radius").canConvertToInt() ? node.get("radius").asInt() : null;

		return RegexChatTrigger.builder()
				.type(TriggerType.REGEX)
				.pattern(regex)
				.strip(strip)
				.radius(radius)
				.build();
	}

	private CommandChatTrigger buildCommand(JsonNode node) {
		String command = node.get("command").asText();
		// strip is not applicable to command triggers; force false
		Integer radius = node.has("radius") && node.get("radius").canConvertToInt() ? node.get("radius").asInt() : null;

		return CommandChatTrigger.builder()
				.type(TriggerType.COMMAND)
				.command(command)
				.strip(false)
				.radius(radius)
				.build();
	}
}


