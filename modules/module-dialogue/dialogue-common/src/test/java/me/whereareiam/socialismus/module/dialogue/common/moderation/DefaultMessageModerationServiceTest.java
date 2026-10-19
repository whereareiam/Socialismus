package me.whereareiam.socialismus.module.dialogue.common.moderation;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.common.DialogueNotices;
import me.whereareiam.socialismus.module.dialogue.common.TestDataBuilder;
import me.whereareiam.socialismus.module.dialogue.common.store.InMemoryPreferencesStore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DefaultMessageModerationServiceTest {
	private final DialogueSettings settings = TestDataBuilder.defaultSettings();
	private final InMemoryPreferencesStore spies = new InMemoryPreferencesStore();
	private final DialogueNotices notices = mock(DialogueNotices.class);
	private final DefaultMessageModerationService service = new DefaultMessageModerationService(() -> settings, spies, notices);
	private final SocialismusPlayer staff = TestDataBuilder.mockPlayer("Staff");

	@Test
	void togglesSpying() {
		service.toggleSpy(staff);
		assertTrue(spies.get(staff.getUniqueId()).isSpying());
		verify(notices).spyToggled(staff, true);

		service.toggleSpy(staff);
		assertFalse(spies.get(staff.getUniqueId()).isSpying());
		verify(notices).spyToggled(staff, false);
	}

	@Test
	void privacyModeRefusesSpying() {
		settings.getModeration().setPrivacy(true);

		service.toggleSpy(staff);

		assertFalse(spies.get(staff.getUniqueId()).isSpying());
		verify(notices).spyUnavailable(staff);
	}
}
