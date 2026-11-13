package me.whereareiam.socialismus.common.config.defaults.chat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatFormat;
import me.whereareiam.socialismus.model.chat.trigger.SymbolChatTrigger;
import me.whereareiam.socialismus.type.PlatformType;
import me.whereareiam.socialismus.type.chat.TriggerType;

import java.util.ArrayList;
import java.util.List;

import static me.whereareiam.socialismus.common.config.defaults.chat.RequirementLines.block;
import static me.whereareiam.socialismus.common.config.defaults.chat.RequirementLines.denied;
import static me.whereareiam.socialismus.common.config.defaults.chat.RequirementLines.line;
import static me.whereareiam.socialismus.common.config.defaults.chat.RequirementLines.list;
import static me.whereareiam.socialismus.common.config.defaults.chat.RequirementLines.not;

/**
 * Chats written into the defaults to show what {@code requires} and {@code reaches} can say, each turned off: a
 * chat that is not enabled is neither loaded nor checked, so a placeholder plugin it names need not be installed.
 * Their texts are phrases of {@code phrases.yml} they refer to with {@code <ref:...>}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ShowcaseChats {
	static List<Chat> all() {
		List<Chat> chats = new ArrayList<>(List.of(town(), staff(), language()));
		if (!PlatformType.isGameServer()) return chats;

		// A proxy knows neither positions nor worlds.
		chats.addAll(0, List.of(shout(), world()));

		return chats;
	}

	private static Chat shout() {
		return showcase(
				"shout",
				"?",
				"{clear}<gray>[Shout] {playerName}: <yellow>{message}",
				Requirements.none(),
				RequirementLines.of(denied(line("nearby", "500"), "<ref:shout-unheard>"))
		);
	}

	private static Chat world() {
		return showcase(
				"world",
				"#",
				"{clear}<gray>[{world}] {playerName}: <white>{message}",
				Requirements.none(),
				RequirementLines.of(line("same", "world"))
		);
	}

	private static Chat town() {
		String town = "%townyadvanced_town%";

		return showcase(
				"town",
				"+",
				"{clear}<green>[Town] {playerName}: <white>{message}",
				RequirementLines.of(denied(not(line("placeholder", block("text", town, "is", ""))), "<ref:town-needed>")),
				RequirementLines.of(line("same", block("placeholder", town)))
		);
	}

	private static Chat staff() {
		return showcase(
				"staff",
				"*",
				"{clear}<red>[Staff] {playerName}: <white>{message}",
				RequirementLines.of(line("permission", "socialismus.chat.staff")),
				RequirementLines.of(line("permission", "socialismus.chat.staff"), line("sees", list("recipient", "sender")))
		);
	}

	private static Chat language() {
		return showcase(
				"deutsch",
				"de:",
				"{clear}<gold>[DE] {playerName}: <white>{message}",
				Requirements.none(),
				RequirementLines.of(line("language", list("de")))
		);
	}

	private static Chat showcase(String id, String symbol, String format, Requirements requires, Requirements reaches) {
		return Chat.builder()
				.id(id)
				.priority(1)
				.enabled(false)
				.triggers(List.of(SymbolChatTrigger.builder()
						.type(TriggerType.SYMBOL)
						.symbol(symbol)
						.strip(true)
						.build()))
				.formats(List.of(new ChatFormat(format, Requirements.none())))
				.requires(requires)
				.reaches(reaches)
				.build();
	}
}
