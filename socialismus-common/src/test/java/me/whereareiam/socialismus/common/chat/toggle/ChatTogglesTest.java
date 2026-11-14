package me.whereareiam.socialismus.common.chat.toggle;

import me.whereareiam.socialismus.common.config.SerializationServiceAdapter;
import me.whereareiam.socialismus.common.store.DefaultStores;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChatTogglesTest {
	private final DefaultStores stores = new DefaultStores(new SerializationServiceAdapter());
	private final ChatToggles toggles = new ChatToggles(stores);
	private final UUID bob = UUID.randomUUID();

	@Test
	void keepsAChatTurnedOffInTheSharedStoreUntilItIsTurnedBackOn() {
		toggles.setOff(bob, "global", true);

		assertTrue(toggles.isOff(bob, "global"));
		assertFalse(toggles.isOff(bob, "staff"));
		assertEquals(Map.of(bob + "/global", true), stores.of(ChatToggles.STORE).entries());

		toggles.setOff(bob, "global", false);

		assertFalse(toggles.isOff(bob, "global"));
		assertTrue(stores.of(ChatToggles.STORE).entries().isEmpty());
	}

	@Test
	void sharesItsStoreAndKeepsItAcrossRestarts() {
		assertTrue(ChatToggles.STORE.isReplicated());
		assertTrue(ChatToggles.STORE.isPersistent());
	}
}
