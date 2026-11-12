package me.whereareiam.socialismus.common.requirement.type;

import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ContentConditionTest {
	private final SocialismusPlayer player = mock(SocialismusPlayer.class);

	@Test
	void content_ContainsIgnoringCase_AndMatchesSomewhere() {
		ChatMessage message = ChatMessage.builder().id(1).content(Component.text("Join discord.GG/abc NOW")).build();
		ContentCondition content = new ContentCondition();

		assertTrue(content.holds(message, new ContentCondition.Settings(List.of("discord.gg"), null)).passed());
		assertTrue(content.holds(message, new ContentCondition.Settings(null, Pattern.compile("NOW$"))).passed());
		assertFalse(content.holds(message, new ContentCondition.Settings(List.of("discord.gg"), Pattern.compile("^now"))).passed());
	}

	private static Map<String, String> values(Outcome outcome) {
		return outcome.reason().orElseThrow().values();
	}

	private static String key(String condition, Outcome outcome) {
		return Reason.key(condition, outcome.reason().orElseThrow().getClass());
	}
}
