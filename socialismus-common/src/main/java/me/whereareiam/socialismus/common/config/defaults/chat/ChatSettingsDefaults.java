package me.whereareiam.socialismus.common.config.defaults.chat;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.model.chat.ChatSettings;

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
		history.setPermission("socialismus.admin");
		history.setBypassPermission("socialismus.admin");
		chatSettings.setHistory(history);

		return chatSettings;
	}
}
