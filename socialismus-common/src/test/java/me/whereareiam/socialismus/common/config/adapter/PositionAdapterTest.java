package me.whereareiam.socialismus.common.config.adapter;

import me.whereareiam.configura.Config;
import me.whereareiam.configura.Configura;
import me.whereareiam.configura.type.Format;
import me.whereareiam.socialismus.common.config.SocialismusConfiguraModule;
import me.whereareiam.socialismus.model.position.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PositionAdapterTest {
	@Test
	void roundTripJsonKeepsCoordinates() {
		Configura configura = Config.builder()
				.format(Format.JSON)
				.module(new SocialismusConfiguraModule())
				.build();
		Position original = new Position(12.5, 64.0, -3.25);

		byte[] bytes = configura.writeBytes(original);
		Position restored = configura.read(bytes, Position.class);

		assertNotNull(restored);
		assertEquals(12.5, restored.getX(), 0.0001);
		assertEquals(64.0, restored.getY(), 0.0001);
		assertEquals(-3.25, restored.getZ(), 0.0001);
	}
}
