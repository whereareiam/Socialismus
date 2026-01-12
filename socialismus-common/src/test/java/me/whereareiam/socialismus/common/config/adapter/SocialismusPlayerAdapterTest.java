package me.whereareiam.socialismus.common.config.adapter;

import me.whereareiam.configura.node.Node;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.player.SyncedSocialismusPlayer;
import me.whereareiam.socialismus.model.position.Position;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SocialismusPlayerAdapterTest {
	@Test
	void roundTripNodeKeepsPlayerData() {
		SocialismusPlayerAdapter adapter = new SocialismusPlayerAdapter();
		UUID id = UUID.randomUUID();
		SyncedSocialismusPlayer original = new SyncedSocialismusPlayer(
				id,
				"sender",
				"server-a",
				"world",
				new Position(12.5, 64.0, -3.25),
				new Position(12.5, 65.62, -3.25)
		);

		Node node = adapter.serializeNode(original);
		SocialismusPlayer restored = adapter.deserializeNode(node);

		assertNotNull(restored);
		assertInstanceOf(SyncedSocialismusPlayer.class, restored);
		assertEquals(id, restored.getUniqueId());
		assertEquals("sender", restored.getUsername());
		assertEquals("server-a", restored.getServer());
		assertEquals("world", restored.getLocation());
		assertPosition(restored.getPosition(), 12.5, 64.0, -3.25);
		assertPosition(restored.getEyePosition(), 12.5, 65.62, -3.25);
	}

	private void assertPosition(Position position, double x, double y, double z) {
		assertNotNull(position);
		assertEquals(x, position.getX(), 0.0001);
		assertEquals(y, position.getY(), 0.0001);
		assertEquals(z, position.getZ(), 0.0001);
	}
}
