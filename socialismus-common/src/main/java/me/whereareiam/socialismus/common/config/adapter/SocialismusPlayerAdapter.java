package me.whereareiam.socialismus.common.config.adapter;

import me.whereareiam.configura.TypeAdapter;
import me.whereareiam.configura.node.Node;
import me.whereareiam.configura.node.NullNode;
import me.whereareiam.configura.node.NumberNode;
import me.whereareiam.configura.node.ObjectNode;
import me.whereareiam.configura.node.StringNode;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.player.SyncedSocialismusPlayer;
import me.whereareiam.socialismus.model.position.Position;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class SocialismusPlayerAdapter implements TypeAdapter<SocialismusPlayer> {
	@Override
	public SocialismusPlayer deserialize(String value) {
		if (value == null || value.isBlank()) return null;
		UUID uniqueId = UUID.fromString(value.trim());

		return new SyncedSocialismusPlayer(uniqueId, value.trim());
	}

	@Override
	public String serialize(SocialismusPlayer value) {
		if (value == null) return null;
		return value.getUniqueId().toString();
	}

	@Override
	public SocialismusPlayer deserializeNode(Node node) {
		if (node == null || node.isNull()) return null;
		if (node instanceof StringNode stringNode)
			return deserialize(stringNode.getValue());

		if (!(node instanceof ObjectNode objectNode))
			throw new IllegalArgumentException("SocialismusPlayer must be an object");

		Map<String, Node> values = objectNode.getValues();
		UUID uniqueId = readUuid(values.get("uniqueId"));
		if (uniqueId == null) throw new IllegalArgumentException("SocialismusPlayer uniqueId is required");

		String username = readString(values.get("username"));
		if (username == null) username = "";

		String server = readString(values.get("server"));
		String location = readString(values.get("location"));
		Position position = readPosition(values.get("position"));
		Position eyePosition = readPosition(values.get("eyePosition"));

		return new SyncedSocialismusPlayer(uniqueId, username, server, location, position, eyePosition);
	}

	@Override
	public Node serializeNode(SocialismusPlayer value) {
		if (value == null) return NullNode.instance();

		Map<String, Node> values = new LinkedHashMap<>();
		values.put("uniqueId", new StringNode(value.getUniqueId().toString()));
		values.put("username", new StringNode(value.getUsername()));
		values.put("server", stringNode(value.getServer()));
		values.put("location", stringNode(value.getLocation()));
		values.put("position", positionNode(value.getPosition()));
		values.put("eyePosition", positionNode(value.getEyePosition()));

		return new ObjectNode(values);
	}

	private Node stringNode(String value) {
		return value == null ? NullNode.instance() : new StringNode(value);
	}

	private Node positionNode(Position position) {
		if (position == null) return NullNode.instance();
		Map<String, Node> values = new LinkedHashMap<>();
		values.put("x", new NumberNode(position.getX()));
		values.put("y", new NumberNode(position.getY()));
		values.put("z", new NumberNode(position.getZ()));
		return new ObjectNode(values);
	}

	private UUID readUuid(Node node) {
		String value = readString(node);
		if (value == null || value.isBlank()) return null;
		return UUID.fromString(value.trim());
	}

	private String readString(Node node) {
		if (node == null || node.isNull()) return null;
		if (node instanceof StringNode stringNode) return stringNode.getValue();
		if (node instanceof NumberNode numberNode && numberNode.getValue() != null) {
			return numberNode.getValue().toString();
		}
		return null;
	}

	private Position readPosition(Node node) {
		if (!(node instanceof ObjectNode objectNode)) return null;
		Map<String, Node> values = objectNode.getValues();
		Double x = readDouble(values.get("x"));
		Double y = readDouble(values.get("y"));
		Double z = readDouble(values.get("z"));
		if (x == null || y == null || z == null) return null;
		return new Position(x, y, z);
	}

	private Double readDouble(Node node) {
		if (node == null || node.isNull()) return null;
		if (node instanceof NumberNode numberNode && numberNode.getValue() != null) {
			return numberNode.getValue().doubleValue();
		}
		if (node instanceof StringNode stringNode) {
			String value = stringNode.getValue();
			if (value == null || value.isBlank()) return null;
			return Double.parseDouble(value);
		}
		return null;
	}
}
