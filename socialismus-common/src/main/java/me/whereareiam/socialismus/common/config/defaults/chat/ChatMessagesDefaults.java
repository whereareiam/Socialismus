package me.whereareiam.socialismus.common.config.defaults.chat;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.model.chat.ChatMessages;

@Singleton
public class ChatMessagesDefaults implements DefaultsProvider<ChatMessages> {
	@Override
	public ChatMessages supply(ChatMessages chatMessages) {
		chatMessages.setNoChatMatch("{prefix}<white>No chat matching criteria found.");
		chatMessages.setNoFormatMatch("{prefix}<white>No format matching criteria found.");
		chatMessages.setNoFallbackChat("{prefix}<white>No fallback chat found.");
		chatMessages.setEmptyMessage("{prefix}<white>You need to write a message.");

		ChatMessages.ClearFormat clearFormat = new ChatMessages.ClearFormat();
		clearFormat.setFormat("<gray>[<red>X</red>]</gray> ");
		chatMessages.setClearFormat(clearFormat);

		ChatMessages.Toggle toggle = new ChatMessages.Toggle();
		toggle.setOff("{prefix}<white>You no longer see <gray>{chat}</gray> chat.");
		toggle.setOn("{prefix}<white>You see <gray>{chat}</gray> chat again.");
		toggle.setNotToggleable("{prefix}<gray>{chat}</gray> <white>chat cannot be turned off.");
		toggle.setUnknown("{prefix}<white>There is no chat <gray>{chat}</gray>.");
		toggle.setUnavailable("{prefix}<white>Chats cannot be turned off here.");
		chatMessages.setToggle(toggle);

		return chatMessages;
	}
}
