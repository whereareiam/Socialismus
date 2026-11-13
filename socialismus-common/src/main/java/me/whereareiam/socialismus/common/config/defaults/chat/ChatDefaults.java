package me.whereareiam.socialismus.common.config.defaults.chat;

import com.google.inject.Singleton;
import me.whereareiam.conditia.model.Node;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.common.config.dynamic.ChatsConfig;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatFormat;
import me.whereareiam.socialismus.model.chat.trigger.SymbolChatTrigger;
import me.whereareiam.socialismus.model.condition.ConditionLists;
import me.whereareiam.socialismus.type.PlatformType;
import me.whereareiam.socialismus.type.chat.TriggerType;

import java.util.List;

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
								.build()
				),
				List.of(new ChatFormat("{clear}<gray>[F] {playerName}: <white>{message}", Requirements.none())),
				Requirements.none(),
				Requirements.none(),
				true
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
								.build()
				),
				List.of(
						new ChatFormat("{clear}<gray>[L] {playerName}: <white>{message}", Requirements.none()),
						new ChatFormat("{clear}<gray>[L] {playerName}: <gold>{message}", ConditionLists.permission("socialismus.admin"))
				),
				ConditionLists.permission("socialismus.chat.local"),
				ConditionLines.of(
						ConditionLines.line("permission", "socialismus.chat.local"),
						nearby()
				),
				true
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
								.build()
				),
				List.of(
						new ChatFormat("{clear}<gray>[G] {playerName}: <white>{message}", Requirements.none()),
						new ChatFormat("{clear}<gray>[G] {playerName}: <gold>{message}", ConditionLists.permission("socialismus.admin"))
				),
				Requirements.none(),
				Requirements.none(),
				true
		);

		chatsConfig.getChats().addAll(List.of(fallback, local, global));
		chatsConfig.getChats().addAll(ShowcaseChats.all());

		return chatsConfig;
	}

	// A proxy knows no positions, so its local chat is the sender's server.
	private static Node.Mapping nearby() {
		if (PlatformType.isGameServer()) return ConditionLines.line("nearby", "200");

		return ConditionLines.line("same", "server");
	}
}
