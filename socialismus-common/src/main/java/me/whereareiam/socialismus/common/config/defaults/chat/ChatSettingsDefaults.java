package me.whereareiam.socialismus.common.config.defaults.chat;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.model.requirement.type.PermissionRequirement;
import me.whereareiam.socialismus.type.requirement.RequirementConditionType;
import me.whereareiam.socialismus.type.requirement.RequirementOperatorType;

import java.util.List;

@Singleton
public class ChatSettingsDefaults implements DefaultsProvider<ChatSettings> {
	@Override
	public ChatSettings supply(ChatSettings chatSettings) {
		ChatSettings.Notifications notifications = new ChatSettings.Notifications();
		notifications.setNoChat(true);
		notifications.setNoFormat(true);
		notifications.setNoPlayers(false);
		notifications.setNoNearbyPlayers(false);
		notifications.setEmptyMessage(true);
		chatSettings.setNotifications(notifications);

		ChatSettings.FallbackChatSettings fallback = new ChatSettings.FallbackChatSettings();
		fallback.setEnabled(true);
		fallback.setChat("fallback");
		chatSettings.setFallback(fallback);

		ChatSettings.ChatHistorySettings history = new ChatSettings.ChatHistorySettings();
		history.setFillerSize(100);
		history.setHistorySize(1000);
		history.setRequirements(permission("socialismus.admin"));
		history.setBypassRequirements(permission("socialismus.admin"));
		chatSettings.setHistory(history);

		return chatSettings;
	}

	private static RequirementGroup permission(String permission) {
		return RequirementGroup.builder()
				.operator(RequirementOperatorType.AND)
				.groups(RequirementGroup.of(Constants.Requirements.PERMISSION, PermissionRequirement.builder()
						.condition(RequirementConditionType.HAS)
						.expected("true")
						.permissions(List.of(permission))
						.build()))
				.build();
	}
}
