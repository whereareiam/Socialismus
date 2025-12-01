package me.whereareiam.socialismus.common.config.template.chat;

import com.google.inject.Singleton;
import me.whereareiam.configura.TemplateProvider;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatFormat;
import me.whereareiam.socialismus.model.chat.trigger.SymbolChatTrigger;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.model.requirement.type.PermissionRequirement;
import me.whereareiam.socialismus.type.chat.Participants;
import me.whereareiam.socialismus.type.chat.TriggerType;
import me.whereareiam.socialismus.type.requirement.RequirementConditionType;
import me.whereareiam.socialismus.type.requirement.RequirementOperatorType;
import me.whereareiam.socialismus.type.requirement.RequirementType;
import me.whereareiam.socialismus.common.config.dynamic.ChatsConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ChatTemplate implements TemplateProvider<ChatsConfig> {
	@Override
	public ChatsConfig supply(ChatsConfig chatsConfig) {
		// Default values
		Chat fallback = new Chat(
				"fallback",
				0,
				true,
				List.of(
						SymbolChatTrigger.builder()
								.type(TriggerType.SYMBOL)
								.symbol("")
								.strip(true)
								.radius(0)
								.build()
				),
				List.of(
						new ChatFormat(
								"{clear}<gray>[F] {playerName}: <white>{message}",
								new HashMap<>()
						)
				),
				new HashMap<>()
		);

		Chat local = new Chat(
				"local",
				0,
				true,
				List.of(
						SymbolChatTrigger.builder()
								.type(TriggerType.SYMBOL)
								.symbol("")
								.strip(true)
								.radius(200)
								.build()
				),
				List.of(
						new ChatFormat(
								"{clear}<gray>[L] {playerName}: <white>{message}",
								new HashMap<>()
						),
						new ChatFormat(
								"{clear}<gray>[L] {playerName}: <gold>{message}",
								Map.of(
										Participants.SENDER,
										RequirementGroup.builder()
												.operator(RequirementOperatorType.AND)
												.groups(Map.of(
														RequirementType.PERMISSION, PermissionRequirement.builder()
																.permissions(List.of("socialismus.admin"))
																.condition(RequirementConditionType.HAS)
																.expected("true")
																.build()
												)).build()
								)
						)
				),
				Map.of(
						Participants.SENDER, RequirementGroup.builder()
								.operator(RequirementOperatorType.AND)
								.groups(Map.of(
										RequirementType.PERMISSION, PermissionRequirement.builder()
												.permissions(List.of("socialismus.chat.local"))
												.condition(RequirementConditionType.HAS)
												.expected("true")
												.build()
								)).build(),
						Participants.RECIPIENT, RequirementGroup.builder()
								.operator(RequirementOperatorType.AND)
								.groups(Map.of(
										RequirementType.PERMISSION, PermissionRequirement.builder()
												.permissions(List.of("socialismus.chat.local"))
												.condition(RequirementConditionType.HAS)
												.expected("true")
												.build()
								)).build()
				)
		);

		Chat global = new Chat(
				"global",
				1,
				true,
				List.of(
						SymbolChatTrigger.builder()
								.type(TriggerType.SYMBOL)
								.symbol("!")
								.strip(true)
								.radius(0)
								.build()
				),
				List.of(
						new ChatFormat(
								"{clear}<gray>[G] {playerName}: <white>{message}",
								new HashMap<>()
						),
						new ChatFormat(
								"{clear}<gray>[G] {playerName}: <gold>{message}",
								Map.of(
										Participants.SENDER,
										RequirementGroup.builder()
												.operator(RequirementOperatorType.AND)
												.groups(Map.of(
														RequirementType.PERMISSION, PermissionRequirement.builder()
																.permissions(List.of("socialismus.admin"))
																.condition(RequirementConditionType.HAS)
																.expected("true")
																.build()
												)).build()
								)
						)
				),
				new HashMap<>()
		);

		chatsConfig.getChats().addAll(List.of(fallback, local, global));

		return chatsConfig;
	}
}