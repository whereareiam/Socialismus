package me.whereareiam.socialismus.common.config.template.chat;

import com.google.inject.Singleton;
import me.whereareiam.configura.TemplateProvider;
import me.whereareiam.socialismus.api.model.chat.ChatSettings;

@Singleton
public class ChatSettingsTemplate implements TemplateProvider<ChatSettings> {
	@Override
	public ChatSettings supply(ChatSettings chatSettings) {
		// Default values
		chatSettings.setNotifyNoChat(true);
		chatSettings.setNotifyNoFormat(true);
		chatSettings.setNotifyNoPlayers(false);
		chatSettings.setNotifyNoNearbyPlayers(false);

		ChatSettings.FallbackChatSettings fallback = new ChatSettings.FallbackChatSettings();
		fallback.setEnabled(true);
		fallback.setChatId("fallback");

		chatSettings.setFallback(fallback);

		ChatSettings.ChatHistorySettings history = new ChatSettings.ChatHistorySettings();
		history.setFillerSize(100);
		history.setHistorySize(1000);
		history.setPermission("socialismus.admin");
		history.setBypassPermission("socialismus.admin");

		chatSettings.setHistory(history);

		ChatSettings.SynchronizationSettings synchronization = new ChatSettings.SynchronizationSettings();
		synchronization.setEnabled(false);
		synchronization.setPreserveFormat(true);
		synchronization.setClearHistory(false);

		chatSettings.setSynchronization(synchronization);

		return chatSettings;
	}
}
