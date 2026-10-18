package me.whereareiam.socialismus.common.config.migration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.whereareiam.strata.MigrationAction;
import me.whereareiam.strata.adapter.configura.ConfigContext;
import org.jetbrains.annotations.NotNull;

/**
 * Turns the permissions of chat history in {@code chat/settings} into requirement groups: {@code permission}, the
 * permission of the players who may clear messages, becomes {@code requirements}, and {@code bypassPermission}, the
 * permission of the players whose messages nobody clears, becomes {@code bypassRequirements}. Each becomes a group
 * that asks for that permission alone, so the same players keep the same rights; a setting that is not there is
 * left to the defaults.
 */
final class ChatHistoryRequirementsMigration implements MigrationAction<ConfigContext> {
	static final int VERSION = 4;
	static final String NAME = "chat-history-requirements";

	private static final JsonNodeFactory NODES = JsonNodeFactory.instance;

	private final String chatSettings;

	ChatHistoryRequirementsMigration(@NotNull String extension) {
		this.chatSettings = "chat/settings" + extension;
	}

	@Override
	public void apply(@NotNull ConfigContext files) {
		convert(files, "permission", "requirements");
		convert(files, "bypassPermission", "bypassRequirements");
	}

	private void convert(ConfigContext files, String permission, String requirements) {
		JsonNode value = files.remove(chatSettings, "/history/" + permission);
		if (value == null || !value.isTextual()) return;

		files.put(chatSettings, "/history/" + requirements, permissionGroup(value.asText()));
	}

	private static ObjectNode permissionGroup(String permission) {
		ObjectNode requirement = NODES.objectNode()
				.put("condition", "HAS")
				.put("expected", "true");
		requirement.putArray("permissions").add(permission);

		ObjectNode group = NODES.objectNode().put("operator", "AND");
		group.putObject("groups").set("socialismus:permission", requirement);

		return group;
	}
}
