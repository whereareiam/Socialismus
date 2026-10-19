package me.whereareiam.socialismus.module.dialogue.common.store;

import me.whereareiam.socialismus.module.dialogue.api.ConversationStore;
import me.whereareiam.socialismus.module.dialogue.api.IgnoreStore;
import me.whereareiam.socialismus.module.dialogue.api.PreferencesStore;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DialogueStoresTest {
	private static final UUID ALICE = UUID.randomUUID();

	private final DialogueStores stores = new DialogueStores(
			new InMemoryPreferencesStore(),
			new InMemoryIgnoreStore(),
			new InMemoryConversationStore()
	);
	private final SelectedPreferencesStore preferences = new SelectedPreferencesStore(stores);

	@Test
	void theSelectedStoresUseTheStoresChosenAndGoBackToMemory() {
		preferences.update(ALICE, current -> current.withMessagesOff(true));
		PreferencesStore replicated = mock(PreferencesStore.class);

		stores.use(replicated, mock(IgnoreStore.class), mock(ConversationStore.class));
		preferences.get(ALICE);
		verify(replicated).get(ALICE);

		stores.reset();
		assertTrue(preferences.get(ALICE).isMessagesOff());
	}

	@Test
	void memoryForgetsAPlayerWithoutPreferences() {
		preferences.update(ALICE, current -> current.withSpying(true));
		preferences.update(ALICE, current -> current.withSpying(false));

		assertFalse(preferences.get(ALICE).isSpying());
	}
}
