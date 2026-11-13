package me.whereareiam.socialismus.common.condition.type;

import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.integration.placeholder.PlaceholderIntegration;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.type.PlaceholderResolutionMode;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlaceholderConditionTest {
	private final SocialismusPlayer player = mock(SocialismusPlayer.class);

	@Test
	void placeholder_ComparesNumbers() {
		PlaceholderIntegration placeholders = mock(PlaceholderIntegration.class);
		when(placeholders.isAvailable()).thenReturn(true);
		when(placeholders.resolutionMode()).thenReturn(PlaceholderResolutionMode.SINGLE_PASS);
		when(placeholders.resolve(any(UUID.class), anyString())).thenReturn("150.5");
		when(player.getUniqueId()).thenReturn(UUID.randomUUID());
		PlaceholderCondition placeholder = new PlaceholderCondition(new PlaceholderReader(() -> Set.<Integration>of(placeholders)));

		Outcome above = placeholder.holds(player, new PlaceholderCondition.Settings("%balance%", null, null, 100.0, null));
		Outcome below = placeholder.holds(player, new PlaceholderCondition.Settings("%balance%", null, null, null, 100.0));

		assertTrue(above.passed());
		assertEquals("placeholder.mismatch", key("placeholder", below));
		assertEquals(Map.of("text", "%balance%", "value", "150.5"), values(below));
	}

	@Test
	void placeholder_FailsWithoutAPlaceholderPlugin() {
		PlaceholderCondition placeholder = new PlaceholderCondition(new PlaceholderReader(Set::of));

		assertFalse(placeholder.holds(player, new PlaceholderCondition.Settings("%x%", "1", null, null, null)).passed());
	}

	private static Map<String, String> values(Outcome outcome) {
		return outcome.reason().orElseThrow().values();
	}

	private static String key(String condition, Outcome outcome) {
		return Reason.key(condition, outcome.reason().orElseThrow().getClass());
	}
}
