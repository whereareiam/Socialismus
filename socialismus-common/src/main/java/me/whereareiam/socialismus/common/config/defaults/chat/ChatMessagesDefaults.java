package me.whereareiam.socialismus.common.config.defaults.chat;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.model.config.message.RequirementReasons;
import me.whereareiam.socialismus.type.chat.Participants;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
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

		Map<String, RequirementReasons> sender = new LinkedHashMap<>();
		sender.put(Constants.Requirements.PERMISSION.getFullKey(), reasons(
				"{prefix}<white>You may not write in <gray>{chat}</gray> chat.</white>",
				null
		));
		sender.put(Constants.Requirements.WORLD.getFullKey(), reasons(
				"{prefix}<white><gray>{chat}</gray> chat only works in <gray>{worlds}</gray>.</white>",
				"{prefix}<white><gray>{chat}</gray> chat does not work in <gray>{world}</gray>.</white>"
		));
		sender.put(Constants.Requirements.SERVER.getFullKey(), reasons(
				"{prefix}<white><gray>{chat}</gray> chat only works on <gray>{servers}</gray>.</white>",
				"{prefix}<white><gray>{chat}</gray> chat does not work on <gray>{server}</gray>.</white>"
		));
		Map<Participants, Map<String, RequirementReasons>> requirements = new EnumMap<>(Participants.class);
		requirements.put(Participants.SENDER, sender);
		chatMessages.setRequirements(requirements);

		ChatMessages.ClearFormat clearFormat = new ChatMessages.ClearFormat();
		clearFormat.setFormat("<gray>[<red>X</red>]</gray> ");
		chatMessages.setClearFormat(clearFormat);

		return chatMessages;
	}

	private static RequirementReasons reasons(String required, @Nullable String forbidden) {
		RequirementReasons reasons = new RequirementReasons();
		reasons.setRequired(required);
		reasons.setForbidden(forbidden);

		return reasons;
	}
}
