package me.whereareiam.socialismus.common.config.adapter;

import me.whereareiam.configura.Config;
import me.whereareiam.configura.Configura;
import me.whereareiam.configura.type.Format;
import me.whereareiam.socialismus.common.config.SocialismusConfiguraModule;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.player.SyncedSocialismusPlayer;
import me.whereareiam.socialismus.model.position.Position;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SocialismusPlayerAdapterTest {
	@Test
	void roundTripJsonKeepsPlayerData() {
		Configura configura = Config.builder()
				.format(Format.JSON)
				.module(new SocialismusConfiguraModule())
				.build();
		UUID id = UUID.randomUUID();
		SyncedSocialismusPlayer original = new SyncedSocialismusPlayer(
				id,
				"sender",
				"server-a",
				"world",
				new Position(12.5, 64.0, -3.25),
				new Position(12.5, 65.62, -3.25)
		);

		byte[] bytes = configura.writeBytes(original);
		SocialismusPlayer restored = configura.read(bytes, SocialismusPlayer.class);

		assertNotNull(restored);
		assertInstanceOf(SyncedSocialismusPlayer.class, restored);
		assertEquals(id, restored.getUniqueId());
		assertEquals("sender", restored.getUsername());
		assertEquals("server-a", restored.getServer());
		assertEquals("world", restored.getLocation());
		assertPosition(restored.getPosition(), 64.0);
		assertPosition(restored.getEyePosition(), 65.62);
	}

	private void assertPosition(Position position, double y) {
		assertNotNull(position);
		assertEquals(12.5, position.getX(), 0.0001);
		assertEquals(y, position.getY(), 0.0001);
		assertEquals(-3.25, position.getZ(), 0.0001);
	}
}
