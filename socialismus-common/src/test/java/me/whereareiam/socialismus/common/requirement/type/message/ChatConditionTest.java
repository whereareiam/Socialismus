package me.whereareiam.socialismus.common.requirement.type.message;

import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.chat.trigger.SymbolChatTrigger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.type.chat.TriggerType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ChatConditionTest {
	private final SocialismusPlayer player = mock(SocialismusPlayer.class);

	@Test
	void chatAndTrigger_AreAboutTheMessageBeingSent() {
		ChatMessage message = ChatMessage.builder()
				.id(1)
				.chat(Chat.builder().id("local").build())
				.trigger(SymbolChatTrigger.builder().type(TriggerType.SYMBOL).symbol("").build())
				.build();

		assertTrue(new ChatCondition().holds(message, new ChatCondition.Settings(List.of("local", "global"))).passed());
		assertEquals("chat.outside", key("chat", new ChatCondition().holds(message, new ChatCondition.Settings(List.of("global")))));
		assertTrue(new TriggerCondition().holds(message, new TriggerCondition.Settings(List.of(TriggerType.SYMBOL))).passed());
		assertFalse(new TriggerCondition().holds(message, new TriggerCondition.Settings(List.of(TriggerType.REGEX))).passed());
		assertFalse(new ChatCondition().holds(ChatMessage.builder().id(2).build(), new ChatCondition.Settings(List.of("local"))).passed());
	}

	private static Map<String, String> values(Outcome outcome) {
		return outcome.reason().orElseThrow().values();
	}

	private static String key(String condition, Outcome outcome) {
		return Reason.key(condition, outcome.reason().orElseThrow().getClass());
	}
}
