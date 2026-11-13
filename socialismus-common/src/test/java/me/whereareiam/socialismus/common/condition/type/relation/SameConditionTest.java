package me.whereareiam.socialismus.common.condition.type.relation;

import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.socialismus.common.condition.type.PlaceholderReader;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.integration.placeholder.PlaceholderIntegration;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.type.PlaceholderResolutionMode;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SameConditionTest {
	private final SocialismusPlayer alice = player();
	private final SocialismusPlayer bob = player();
	private final PlaceholderIntegration placeholders = mock(PlaceholderIntegration.class);
	private final SameCondition same = new SameCondition(
			() -> mock(PlayerRegistry.class),
			new PlaceholderReader(() -> Set.<Integration>of(placeholders))
	);

	@Test
	void sameServer_ComparesTheServersTheProxyNames_AndAnUnknownOneIsSharedWithNobody() {
		when(alice.getServer()).thenReturn("lobby");
		when(bob.getServer()).thenReturn("lobby");

		assertTrue(same.holds(alice, bob, new SameCondition.Settings("server", null)).passed());

		when(alice.getServer()).thenReturn(null);
		when(bob.getServer()).thenReturn(null);

		Outcome unknown = same.holds(alice, bob, new SameCondition.Settings("server", null));
		assertFalse(unknown.passed());
		assertEquals("same.outside", Reason.key("same", unknown.reason().orElseThrow().getClass()));
		assertEquals(Map.of("place", "server"), unknown.reason().orElseThrow().values());
	}

	@Test
	void samePlaceholder_ComparesTheValues_AndTwoEmptyOnesAreNotShared() {
		when(placeholders.isAvailable()).thenReturn(true);
		when(placeholders.resolutionMode()).thenReturn(PlaceholderResolutionMode.SINGLE_PASS);
		when(placeholders.resolve(eq(alice.getUniqueId()), anyString())).thenReturn("Rivendell");
		when(placeholders.resolve(eq(bob.getUniqueId()), anyString())).thenReturn("Rivendell");
		SameCondition.Settings town = new SameCondition.Settings(null, "%townyadvanced_town%");

		Outcome shared = same.holds(alice, bob, town);
		assertTrue(shared.passed());
		assertEquals(Map.of("place", "Rivendell"), shared.reason().orElseThrow().values());

		when(placeholders.resolve(eq(alice.getUniqueId()), anyString())).thenReturn("");
		when(placeholders.resolve(eq(bob.getUniqueId()), anyString())).thenReturn("");

		assertFalse(same.holds(alice, bob, town).passed());
	}

	@Test
	void same_NeedsExactlyOneOfAPlaceAndAPlaceholder() {
		assertThrows(IllegalArgumentException.class, () -> new SameCondition.Settings(null, null));
		assertThrows(IllegalArgumentException.class, () -> new SameCondition.Settings("server", "%x%"));
		assertThrows(IllegalArgumentException.class, () -> new SameCondition.Settings("galaxy", null));
	}

	private static SocialismusPlayer player() {
		SocialismusPlayer player = mock(SocialismusPlayer.class);
		when(player.getUniqueId()).thenReturn(UUID.randomUUID());

		return player;
	}
}
