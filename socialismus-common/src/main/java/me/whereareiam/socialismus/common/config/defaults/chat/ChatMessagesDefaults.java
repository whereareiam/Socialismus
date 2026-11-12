package me.whereareiam.socialismus.common.config.defaults.chat;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.model.chat.ChatMessages;

import java.util.LinkedHashMap;
import java.util.Map;

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

		// Kept in this order in the file.
		Map<String, String> requirements = new LinkedHashMap<>();
		requirements.put("permission.missing", "{prefix}<white>You may not write in <gray>{chat}</gray> chat.</white>");
		requirements.put("world.outside", "{prefix}<white><gray>{chat}</gray> chat only works in <gray>{worlds}</gray>.</white>");
		requirements.put("world.inside", "{prefix}<white><gray>{chat}</gray> chat does not work in <gray>{world}</gray>.</white>");
		requirements.put("server.outside", "{prefix}<white><gray>{chat}</gray> chat only works on <gray>{servers}</gray>.</white>");
		requirements.put("server.inside", "{prefix}<white><gray>{chat}</gray> chat does not work on <gray>{server}</gray>.</white>");
		chatMessages.setRequirements(requirements);

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
