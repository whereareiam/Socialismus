package me.whereareiam.socialismus.module.dialogue.common;

import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.RequirementLists;
import me.whereareiam.socialismus.model.requirement.RequirementParts;
import me.whereareiam.socialismus.model.requirement.RequirementReason;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueMessages;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.common.config.defaults.DialogueMessagesDefaults;
import me.whereareiam.socialismus.service.requirement.RequirementReasonService;
import me.whereareiam.socialismus.type.chat.Participants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MessagingRequirementsTest {
	private final RequirementReasonService reasonService = mock(RequirementReasonService.class);
	private final SocialismusPlayer alice = TestDataBuilder.mockPlayer("Alice");
	private final SocialismusPlayer bob = TestDataBuilder.mockPlayer("Bob");
	private final DialogueMessages messages = new DialogueMessagesDefaults().supply(new DialogueMessages());
	private final DialogueSettings settings = TestDataBuilder.defaultSettings();

	@BeforeEach
	void setUp() {
		settings.getMessaging().setRequires(RequirementLists.permission("dialogue.send"));
		settings.getMessaging().setReaches(RequirementLists.permission("dialogue.receive"));
		when(alice.hasPermission("dialogue.send")).thenReturn(true);
	}

	@Test
	void allowsWhatBothParticipantsMeet() {
		when(bob.hasPermission("dialogue.receive")).thenReturn(true);

		assertTrue(requirements().judge(alice, bob, "Bob").allowed());
	}

	@Test
	void wordsARefusedRecipientWithTheRecipientsWording() {
		RequirementReason reason = RequirementReason.of("Bob may not receive", Map.of());
		when(reasonService.resolve(any(), eq(wording(Participants.RECIPIENT)), eq(Map.of("recipientName", "Bob")))).thenReturn(Optional.of(reason));

		MessagingRequirements.Verdict verdict = requirements().judge(alice, bob, "Bob");

		assertFalse(verdict.allowed());
		assertSame(reason, verdict.reason());
		assertEquals("messaging.reaches[0].permission", refusal().path());
	}

	@Test
	void judgesTheSenderBeforeTheRecipientWithTheSendersWording() {
		when(alice.hasPermission("dialogue.send")).thenReturn(false);

		assertFalse(requirements().judge(alice, bob, "Bob").allowed());

		Verdict.Failed failed = refusal();
		assertEquals("messaging.requires[0].permission", failed.path());
		assertTrue(failed.about(RequirementParts.SENDER));
		verify(reasonService).resolve(failed, wording(Participants.SENDER), Map.of("recipientName", "Bob"));
		verify(bob, never()).hasPermission("dialogue.receive");
	}

	@Test
	void refusesARecipientThisServerDoesNotKnowWithoutAReason() {
		MessagingRequirements.Verdict verdict = requirements().judge(alice, null, "Bob");

		assertFalse(verdict.allowed());
		assertNull(verdict.reason());
	}

	@Test
	void allowsARecipientThisServerDoesNotKnowWhenAnyoneMayReceive() {
		settings.getMessaging().setReaches(me.whereareiam.conditia.model.Requirements.none());

		assertTrue(requirements().judge(alice, null, "Bob").allowed());
	}

	private Map<String, String> wording(Participants participant) {
		return messages.getMessaging().getRequirements().get(participant);
	}

	private Verdict.Failed refusal() {
		ArgumentCaptor<Verdict.Failed> failed = ArgumentCaptor.forClass(Verdict.Failed.class);
		verify(reasonService).resolve(failed.capture(), any(), any());

		return failed.getValue();
	}

	private MessagingRequirements requirements() {
		return new MessagingRequirements(DialogueConditions.bound(settings), reasonService, () -> messages);
	}
}
