package me.whereareiam.socialismus.common.condition.type;

import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PermissionConditionTest {
	private final SocialismusPlayer player = mock(SocialismusPlayer.class);

	@Test
	void permission_FailsWithTheMissingOnes_AndPassesWithTheHeldOnes() {
		when(player.hasPermission("chat.local")).thenReturn(true);
		PermissionCondition permission = new PermissionCondition();

		Outcome missing = permission.holds(player, new PermissionCondition.Settings(List.of("chat.local", "chat.staff")));
		Outcome held = permission.holds(player, new PermissionCondition.Settings(List.of("chat.local")));

		assertEquals(Map.of("permissions", "chat.staff"), values(missing));
		assertEquals("permission.missing", key("permission", missing));
		assertTrue(held.passed());
		assertEquals("permission.held", key("permission", held));
	}

	private static Map<String, String> values(Outcome outcome) {
		return outcome.reason().orElseThrow().values();
	}

	private static String key(String condition, Outcome outcome) {
		return Reason.key(condition, outcome.reason().orElseThrow().getClass());
	}
}
