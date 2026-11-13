package me.whereareiam.socialismus.common.config.defaults.chat;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.condition.ConditionLists;

@Singleton
public class ChatSettingsDefaults implements DefaultsProvider<ChatSettings> {
	@Override
	public ChatSettings supply(ChatSettings chatSettings) {
		ChatSettings.Notifications notifications = new ChatSettings.Notifications();
		notifications.setNoChat(true);
		notifications.setNoFormat(true);
		notifications.setNobody(false);
		notifications.setEmptyMessage(true);
		chatSettings.setNotifications(notifications);

		ChatSettings.FallbackChatSettings fallback = new ChatSettings.FallbackChatSettings();
		fallback.setEnabled(true);
		fallback.setChat("fallback");
		chatSettings.setFallback(fallback);

		ChatSettings.ChatHistorySettings history = new ChatSettings.ChatHistorySettings();
		history.setFillerSize(100);
		history.setHistorySize(1000);
		history.setRequires(ConditionLists.permission("socialismus.admin"));
		history.setBypass(ConditionLists.permission("socialismus.admin"));
		chatSettings.setHistory(history);

		ChatSettings.Toggle toggle = new ChatSettings.Toggle();
		toggle.setEnabled(true);
		toggle.setBypass(ConditionLists.permission("socialismus.chat.toggle.bypass"));
		chatSettings.setToggle(toggle);

		return chatSettings;
	}
}
