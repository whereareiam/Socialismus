package me.whereareiam.socialismus.module.dialogue.common;

import me.whereareiam.socialismus.model.config.message.RequirementReasons;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.model.requirement.RequirementReason;
import me.whereareiam.socialismus.model.requirement.RequirementResult;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueMessages;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.common.config.defaults.DialogueMessagesDefaults;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;
import me.whereareiam.socialismus.service.requirement.RequirementReasonService;
import me.whereareiam.socialismus.type.chat.Participants;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MessagingRequirementsTest {
	private final RequirementEvaluatorService evaluator = mock(RequirementEvaluatorService.class);
	private final RequirementReasonService reasonService = mock(RequirementReasonService.class);
	private final SocialismusPlayer alice = mock(SocialismusPlayer.class);
	private final SocialismusPlayer bob = mock(SocialismusPlayer.class);
	private final RequirementGroup senders = group();
	private final RequirementGroup recipients = group();
	private final DialogueMessages messages = new DialogueMessagesDefaults().supply(new DialogueMessages());

	@Test
	void allowsWhatBothParticipantsMeet() {
		when(evaluator.evaluate(any(RequirementGroup.class), any())).thenReturn(RequirementResult.passed());

		assertTrue(requirements().judge(alice, bob, "Bob").allowed());
	}

	@Test
	void wordsARefusedRecipientWithTheRecipientsWording() {
		RequirementResult refusal = RequirementResult.denied(null, "no");
		RequirementReason reason = RequirementReason.of("no", Map.of());
		Map<String, RequirementReasons> recipientWording = messages.getMessaging().getRequirements().get(Participants.RECIPIENT);
		when(evaluator.evaluate(senders, alice)).thenReturn(RequirementResult.passed());
		when(evaluator.evaluate(recipients, bob)).thenReturn(refusal);
		when(reasonService.resolve(refusal, recipientWording, Map.of("recipientName", "Bob"))).thenReturn(reason);

		MessagingRequirements.Verdict verdict = requirements().judge(alice, bob, "Bob");

		assertFalse(verdict.allowed());
		assertSame(reason, verdict.reason());
	}

	@Test
	void judgesTheSenderBeforeTheRecipient() {
		RequirementResult refusal = RequirementResult.denied(null, "no");
		Map<String, RequirementReasons> senderWording = messages.getMessaging().getRequirements().get(Participants.SENDER);
		when(evaluator.evaluate(senders, alice)).thenReturn(refusal);

		assertFalse(requirements().judge(alice, bob, "Bob").allowed());
		verify(reasonService).resolve(refusal, senderWording, Map.of("recipientName", "Bob"));
		verify(evaluator, never()).evaluate(recipients, bob);
	}

	@Test
	void refusesARecipientThisServerDoesNotKnowWithoutAReason() {
		when(evaluator.evaluate(senders, alice)).thenReturn(RequirementResult.passed());

		MessagingRequirements.Verdict verdict = requirements().judge(alice, null, "Bob");

		assertFalse(verdict.allowed());
		assertNull(verdict.reason());
	}

	private MessagingRequirements requirements() {
		DialogueSettings settings = TestDataBuilder.defaultSettings();
		Map<Participants, RequirementGroup> groups = new EnumMap<>(Participants.class);
		groups.put(Participants.SENDER, senders);
		groups.put(Participants.RECIPIENT, recipients);
		settings.getMessaging().setRequirements(groups);

		return new MessagingRequirements(evaluator, reasonService, () -> settings, () -> messages);
	}

	private static RequirementGroup group() {
		return RequirementGroup.builder().groups(Map.of("socialismus:permission", mock(Requirement.class))).build();
	}
}
