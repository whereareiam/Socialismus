package me.whereareiam.socialismus.common.requirement.type;

import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WorldConditionTest {
	private final SocialismusPlayer player = mock(SocialismusPlayer.class);

	@Test
	void world_TellsTheWorldsOutsideAndTheWorldInside() {
		when(player.getLocation()).thenReturn("world_nether");
		WorldCondition world = new WorldCondition();

		Outcome outside = world.holds(player, new WorldCondition.Settings(List.of("world")));
		Outcome inside = world.holds(player, new WorldCondition.Settings(List.of("world_nether")));

		assertEquals("world.outside", key("world", outside));
		assertEquals(Map.of("worlds", "world", "world", "world_nether"), values(outside));
		assertEquals("world.inside", key("world", inside));
		assertEquals(Map.of("world", "world_nether"), values(inside));
	}

	private static Map<String, String> values(Outcome outcome) {
		return outcome.reason().orElseThrow().values();
	}

	private static String key(String condition, Outcome outcome) {
		return Reason.key(condition, outcome.reason().orElseThrow().getClass());
	}
}
