package me.whereareiam.socialismus.common.condition.type.relation;

import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NearbyConditionTest {
	private final SocialismusPlayer recipient = mock(SocialismusPlayer.class);
	private final SocialismusPlayer sender = mock(SocialismusPlayer.class);
	private final NearbyCondition nearby = new NearbyCondition();

	@Test
	void nearby_HoldsWithinTheBlocks_AndTellsThemWhenFarther() {
		when(recipient.isWithinRange(sender, 200)).thenReturn(true);
		when(recipient.isWithinRange(sender, 20)).thenReturn(false);

		Outcome within = nearby.holds(recipient, sender, new NearbyCondition.Settings(200));
		Outcome farther = nearby.holds(recipient, sender, new NearbyCondition.Settings(20));

		assertTrue(within.passed());
		assertEquals("nearby.inside", Reason.key("nearby", within.reason().orElseThrow().getClass()));
		assertEquals("nearby.outside", Reason.key("nearby", farther.reason().orElseThrow().getClass()));
		assertEquals(Map.of("blocks", "20"), farther.reason().orElseThrow().values());
	}

	@Test
	void nearby_NeedsBlocksAboveZero() {
		assertThrows(IllegalArgumentException.class, () -> new NearbyCondition.Settings(0));
	}
}
