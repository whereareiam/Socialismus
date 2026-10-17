package me.whereareiam.socialismus.common.config.defaults.chat;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.model.chat.ChatSync;

@Singleton
public class ChatSyncDefaults implements DefaultsProvider<ChatSync> {
	@Override
	public ChatSync supply(ChatSync chatSync) {
		chatSync.setEnabled(false);
		chatSync.setPreserveFormat(true);
		chatSync.setClearHistory(false);

		return chatSync;
	}
}
