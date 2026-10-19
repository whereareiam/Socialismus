package me.whereareiam.socialismus.module.dialogue.common.moderation;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.event.DialogueDeliveredEvent;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.common.SenderPrivileges;
import me.whereareiam.socialismus.module.dialogue.common.TestDataBuilder;
import me.whereareiam.socialismus.module.dialogue.common.delivery.MessageRenderer;
import me.whereareiam.socialismus.module.dialogue.common.store.InMemoryPreferencesStore;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SpyingTest {
	private final DialogueSettings settings = TestDataBuilder.defaultSettings();
	private final InMemoryPreferencesStore spies = new InMemoryPreferencesStore();
	private final RequirementEvaluatorService evaluator = mock(RequirementEvaluatorService.class);
	private final PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
	private final MessageRenderer renderer = mock(MessageRenderer.class);
	private final Spying spying = new Spying(() -> settings, spies, new SenderPrivileges(() -> settings, evaluator), playerRegistry, renderer);

	private final SocialismusPlayer alice = TestDataBuilder.mockPlayer("Alice");
	private final SocialismusPlayer bob = TestDataBuilder.mockPlayer("Bob");
	private final SocialismusPlayer staff = TestDataBuilder.mockPlayer("Staff");
	private final SocialismusPlayer carol = TestDataBuilder.mockPlayer("Carol");

	@BeforeEach
	void setUp() {
		when(playerRegistry.getPlayers()).thenReturn(Set.of(alice, bob, staff, carol));
		spies.update(staff.getUniqueId(), current -> current.withSpying(true));
	}

	@Test
	void showsTheMessageToTheSpiesOnly() {
		Message message = TestDataBuilder.messageFrom(alice, "Bob").build();

		spying.onDelivered(new DialogueDeliveredEvent(message, bob));

		verify(renderer).showSpied(message, bob, staff);
		verify(renderer, never()).showSpied(any(), any(), eq(carol));
	}

	@Test
	void aSpyWhoTakesPartSeesOnlyTheirOwnCopy() {
		Message message = TestDataBuilder.messageFrom(staff, "Bob").build();

		spying.onDelivered(new DialogueDeliveredEvent(message, bob));

		verifyNoInteractions(renderer);
	}

	@Test
	void showsNothingInPrivacyMode() {
		settings.getModeration().setPrivacy(true);

		spying.onDelivered(new DialogueDeliveredEvent(TestDataBuilder.messageFrom(alice, "Bob").build(), bob));

		verifyNoInteractions(renderer);
	}

	@Test
	void hidesTheMessagesOfAnExemptSender() {
		spying.onDelivered(new DialogueDeliveredEvent(TestDataBuilder.messageFrom(alice, "Bob").hiddenFromSpies(true).build(), bob));

		verifyNoInteractions(renderer);
	}

	@Test
	void hidesTheMessagesToAnExemptRecipient() {
		when(evaluator.check(settings.getModeration().getSpy().getExempt(), bob)).thenReturn(true);

		spying.onDelivered(new DialogueDeliveredEvent(TestDataBuilder.messageFrom(alice, "Bob").build(), bob));

		verifyNoInteractions(renderer);
	}
}
