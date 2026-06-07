package me.whereareiam.socialismus.common.config.defaults.chat;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.common.config.dynamic.ChatsConfig;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatFormat;
import me.whereareiam.socialismus.model.chat.trigger.SymbolChatTrigger;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.model.requirement.type.PermissionRequirement;
import me.whereareiam.socialismus.type.chat.Participants;
import me.whereareiam.socialismus.type.chat.TriggerType;
import me.whereareiam.socialismus.type.requirement.RequirementConditionType;
import me.whereareiam.socialismus.type.requirement.RequirementOperatorType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ChatDefaults implements DefaultsProvider<ChatsConfig> {
	@Override
	public ChatsConfig supply(ChatsConfig chatsConfig) {
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
				List.of(new ChatFormat("{clear}<gray>[F] {playerName}: <white>{message}", new HashMap<>())),
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
						new ChatFormat("{clear}<gray>[L] {playerName}: <white>{message}", new HashMap<>()),
						new ChatFormat(
								"{clear}<gray>[L] {playerName}: <gold>{message}",
								Map.of(
										Participants.SENDER,
										RequirementGroup.builder()
												.operator(RequirementOperatorType.AND)
												.groups(RequirementGroup.of(
														Constants.Requirements.PERMISSION,
														PermissionRequirement.builder()
																.permissions(List.of("socialismus.admin"))
																.condition(RequirementConditionType.HAS)
																.expected("true")
																.build()
												))
												.build()
								)
						)
				),
				Map.of(
						Participants.SENDER,
						RequirementGroup.builder()
								.operator(RequirementOperatorType.AND)
								.groups(RequirementGroup.of(
										Constants.Requirements.PERMISSION,
										PermissionRequirement.builder()
												.permissions(List.of("socialismus.chat.local"))
												.condition(RequirementConditionType.HAS)
												.expected("true")
												.build()
								))
								.build(),
						Participants.RECIPIENT,
						RequirementGroup.builder()
								.operator(RequirementOperatorType.AND)
								.groups(RequirementGroup.of(
										Constants.Requirements.PERMISSION,
										PermissionRequirement.builder()
												.permissions(List.of("socialismus.chat.local"))
												.condition(RequirementConditionType.HAS)
												.expected("true")
												.build()
								))
								.build()
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
						new ChatFormat("{clear}<gray>[G] {playerName}: <white>{message}", new HashMap<>()),
						new ChatFormat(
								"{clear}<gray>[G] {playerName}: <gold>{message}",
								Map.of(
										Participants.SENDER,
										RequirementGroup.builder()
												.operator(RequirementOperatorType.AND)
												.groups(RequirementGroup.of(
														Constants.Requirements.PERMISSION,
														PermissionRequirement.builder()
																.permissions(List.of("socialismus.admin"))
																.condition(RequirementConditionType.HAS)
																.expected("true")
																.build()
												))
												.build()
								)
						)
				),
				new HashMap<>()
		);

		chatsConfig.getChats().addAll(List.of(fallback, local, global));
		return chatsConfig;
	}
}
