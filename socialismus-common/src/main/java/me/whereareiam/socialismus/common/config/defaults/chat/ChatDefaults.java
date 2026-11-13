package me.whereareiam.socialismus.common.config.defaults.chat;

import com.google.inject.Singleton;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.common.config.dynamic.ChatsConfig;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatFormat;
import me.whereareiam.socialismus.model.chat.ChatParameters;
import me.whereareiam.socialismus.model.chat.trigger.SymbolChatTrigger;
import me.whereareiam.socialismus.model.requirement.RequirementLists;
import me.whereareiam.socialismus.type.chat.TriggerType;

import java.util.List;

@Singleton
public class ChatDefaults implements DefaultsProvider<ChatsConfig> {
	private static final ChatParameters PARAMETERS = new ChatParameters(true, List.of("basic", "full"));

	@Override
	public ChatsConfig supply(ChatsConfig chatsConfig) {
		Chat fallback = new Chat(
				"fallback",
				0,
				true,
				PARAMETERS,
				List.of(
						SymbolChatTrigger.builder()
								.type(TriggerType.SYMBOL)
								.symbol("")
								.strip(true)
								.radius(0)
								.build()
				),
				List.of(new ChatFormat("{clear}<gray>[F] {playerName}: <white>{message}", Requirements.none())),
				Requirements.none(),
				Requirements.none()
		);

		Chat local = new Chat(
				"local",
				0,
				true,
				PARAMETERS,
				List.of(
						SymbolChatTrigger.builder()
								.type(TriggerType.SYMBOL)
								.symbol("")
								.strip(true)
								.radius(200)
								.build()
				),
				List.of(
						new ChatFormat("{clear}<gray>[L] {playerName}: <white>{message}", Requirements.none()),
						new ChatFormat("{clear}<gray>[L] {playerName}: <gold>{message}", RequirementLists.permission("socialismus.admin"))
				),
				RequirementLists.permission("socialismus.chat.local"),
				RequirementLists.permission("socialismus.chat.local")
		);

		Chat global = new Chat(
				"global",
				1,
				true,
				PARAMETERS,
				List.of(
						SymbolChatTrigger.builder()
								.type(TriggerType.SYMBOL)
								.symbol("!")
								.strip(true)
								.radius(0)
								.build()
				),
				List.of(
						new ChatFormat("{clear}<gray>[G] {playerName}: <white>{message}", Requirements.none()),
						new ChatFormat("{clear}<gray>[G] {playerName}: <gold>{message}", RequirementLists.permission("socialismus.admin"))
				),
				Requirements.none(),
				Requirements.none()
		);

		chatsConfig.getChats().addAll(List.of(fallback, local, global));
		return chatsConfig;
	}
}
