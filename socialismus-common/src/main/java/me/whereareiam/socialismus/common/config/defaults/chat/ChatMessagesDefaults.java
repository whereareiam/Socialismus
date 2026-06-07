package me.whereareiam.socialismus.common.config.defaults.chat;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.model.chat.ChatMessages;

@Singleton
public class ChatMessagesDefaults implements DefaultsProvider<ChatMessages> {
	@Override
	public ChatMessages supply(ChatMessages chatMessages) {
		chatMessages.setNoPlayers("{prefix}<white>Your message was not sent because there are no players online.");
		chatMessages.setNoChatMatch("{prefix}<white>No chat matching criteria found.");
		chatMessages.setNoFormatMatch("{prefix}<white>No format matching criteria found.");
		chatMessages.setNoFallbackChat("{prefix}<white>No fallback chat found.");
		chatMessages.setNoNearbyPlayers("{prefix}<white>No nearby players found, within a radius of <gray>{radius}</gray> blocks.");
		chatMessages.setEmptyMessage("{prefix}<white>You need to write a message.");

		ChatMessages.ClearFormat clearFormat = new ChatMessages.ClearFormat();
		clearFormat.setFormat("<gray>[<red>X</red>]</gray> ");
		chatMessages.setClearFormat(clearFormat);

		return chatMessages;
	}
}
