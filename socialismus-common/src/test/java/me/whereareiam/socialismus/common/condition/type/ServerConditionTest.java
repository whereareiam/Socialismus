package me.whereareiam.socialismus.common.condition.type;

import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ServerConditionTest {
	private final SocialismusPlayer player = mock(SocialismusPlayer.class);

	@Test
	void server_FailsWithoutAReasonWhileTheServerIsUnknown() {
		Outcome outcome = new ServerCondition().holds(player, new ServerCondition.Settings(List.of("lobby")));

		assertFalse(outcome.passed());
		assertTrue(outcome.reason().isEmpty());
	}

	private static Map<String, String> values(Outcome outcome) {
		return outcome.reason().orElseThrow().values();
	}

	private static String key(String condition, Outcome outcome) {
		return Reason.key(condition, outcome.reason().orElseThrow().getClass());
	}
}
