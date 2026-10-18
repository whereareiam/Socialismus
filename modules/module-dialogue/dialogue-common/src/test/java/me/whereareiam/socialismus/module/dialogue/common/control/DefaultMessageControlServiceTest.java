package me.whereareiam.socialismus.module.dialogue.common.control;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.IgnoredPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.common.DialogueNotices;
import me.whereareiam.socialismus.module.dialogue.common.TestDataBuilder;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DefaultMessageControlServiceTest {
	private final DialogueSettings settings = TestDataBuilder.defaultSettings();
	private final InMemoryMessageToggleStore toggles = new InMemoryMessageToggleStore();
	private final InMemoryIgnoreStore ignores = new InMemoryIgnoreStore();
	private final RequirementEvaluatorService evaluator = mock(RequirementEvaluatorService.class);
	private final PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
	private final DialogueNotices notices = mock(DialogueNotices.class);
	private final SocialismusPlayer alice = TestDataBuilder.mockPlayer("Alice");
	private final SocialismusPlayer bob = TestDataBuilder.mockPlayer("Bob");

	private DefaultMessageControlService service;

	@BeforeEach
	void setUp() {
		when(playerRegistry.getPlayerData(anyString())).thenReturn(Optional.empty());
		when(playerRegistry.getPlayerData("bob")).thenReturn(Optional.of(bob));
		when(playerRegistry.getPlayerData("alice")).thenReturn(Optional.of(alice));
		RecipientControl control = new RecipientControl(() -> settings, toggles, ignores, evaluator);
		service = new DefaultMessageControlService(() -> settings, toggles, ignores, control, playerRegistry, notices);
	}

	@Test
	void togglesPrivateMessagesOffAndBackOn() {
		service.toggleMessages(alice);
		assertTrue(toggles.isOff(alice.getUniqueId()));
		verify(notices).messagesToggled(alice, true);

		service.toggleMessages(alice);
		assertFalse(toggles.isOff(alice.getUniqueId()));
		verify(notices).messagesToggled(alice, false);
	}

	@Test
	void togglingCanBeTurnedOff() {
		settings.getControl().getToggle().setEnabled(false);

		service.toggleMessages(alice);

		assertFalse(toggles.isOff(alice.getUniqueId()));
		verify(notices).toggleUnavailable(alice);
	}

	@Test
	void ignoresAPlayerAndStopsWhenNamedAgainEvenIfTheyLeft() {
		service.ignore(alice, "bob");
		assertEquals(List.of(new IgnoredPlayer(bob.getUniqueId(), "Bob")), ignores.ignored(alice.getUniqueId()));
		verify(notices).ignoreChanged(alice, "Bob", true);

		when(playerRegistry.getPlayerData("BOB")).thenReturn(Optional.empty());
		service.ignore(alice, "BOB");
		assertTrue(ignores.ignored(alice.getUniqueId()).isEmpty());
		verify(notices).ignoreChanged(alice, "Bob", false);
	}

	@Test
	void aPlayerWhoMeetsTheExemptionCannotBeIgnored() {
		when(evaluator.check(any(), any())).thenReturn(false);
		when(evaluator.check(settings.getControl().getIgnore().getExempt(), bob)).thenReturn(true);

		service.ignore(alice, "bob");

		assertTrue(ignores.ignored(alice.getUniqueId()).isEmpty());
		verify(notices).ignoreExempt(alice, "Bob");
	}

	@Test
	void anUnknownPlayerCannotBeIgnored() {
		service.ignore(alice, "nobody");

		verify(notices).noRecipient(alice, "nobody");
	}

	@Test
	void nobodyIgnoresThemselves() {
		service.ignore(alice, "alice");

		verify(notices).samePlayer(alice);
		assertTrue(ignores.ignored(alice.getUniqueId()).isEmpty());
	}

	@Test
	void showsWhomAPlayerIgnores() {
		ignores.ignore(alice.getUniqueId(), new IgnoredPlayer(bob.getUniqueId(), "Bob"));

		service.showIgnored(alice);

		verify(notices).ignored(alice, List.of(new IgnoredPlayer(bob.getUniqueId(), "Bob")));
	}
}
