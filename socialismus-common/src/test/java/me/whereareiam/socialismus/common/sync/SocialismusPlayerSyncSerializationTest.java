package me.whereareiam.socialismus.common.sync;

import me.whereareiam.configura.Config;
import me.whereareiam.configura.Configura;
import me.whereareiam.configura.type.Format;
import me.whereareiam.socialismus.common.config.SerializationServiceAdapter;
import me.whereareiam.socialismus.common.config.SocialismusConfiguraModule;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.player.SyncedSocialismusPlayer;
import me.whereareiam.socialismus.model.position.Position;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SocialismusPlayerSyncSerializationTest {
	@BeforeAll
	static void configureConfigura() {
		Configura configura = Config.builder()
				.format(Format.JSON)
				.module(new SocialismusConfiguraModule())
				.build();
		Config.setConfigured(configura);
	}

	@Test
	void formattedChatMessageRoundTripKeepsPlayersAndComponents() {
		UUID senderId = UUID.randomUUID();
		SyncedSocialismusPlayer sender = new SyncedSocialismusPlayer(
				senderId,
				"sender",
				"server-a",
				"world",
				new Position(1.0, 2.0, 3.0),
				new Position(1.0, 2.62, 3.0)
		);

		UUID recipientId = UUID.randomUUID();
		SyncedSocialismusPlayer recipient = new SyncedSocialismusPlayer(
				recipientId,
				"recipient",
				"server-a",
				"world",
				new Position(4.0, 5.0, 6.0),
				null
		);

		FormattedChatMessage message = FormattedChatMessage.builder()
				.id(42)
				.sender(sender)
				.recipients(List.of(recipient))
				.content(Component.text("@all hello"))
				.format(Component.text("formatted"))
				.origin("origin-a")
				.build();

		SerializationServiceAdapter service = new SerializationServiceAdapter();
		byte[] data = service.serialize(message);
		FormattedChatMessage restored = service.deserialize(data, FormattedChatMessage.class);

		assertNotNull(restored);
		assertNotNull(restored.getSender());
		assertInstanceOf(SyncedSocialismusPlayer.class, restored.getSender());
		assertEquals(senderId, restored.getSender().getUniqueId());
		assertEquals("sender", restored.getSender().getUsername());
		assertEquals("server-a", restored.getSender().getServer());
		assertEquals("world", restored.getSender().getLocation());
		assertEquals(Component.text("@all hello"), restored.getContent());
		assertEquals(Component.text("formatted"), restored.getFormat());

		assertNotNull(restored.getRecipients());
		assertEquals(1, restored.getRecipients().size());
		SocialismusPlayer restoredRecipient = restored.getRecipients().iterator().next();
		assertInstanceOf(SyncedSocialismusPlayer.class, restoredRecipient);
		assertEquals(recipientId, restoredRecipient.getUniqueId());
	}
}
