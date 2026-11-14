package me.whereareiam.socialismus.module.dialogue.common;

import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.model.condition.ConditionLists;
import me.whereareiam.socialismus.model.condition.ConditionParts;
import me.whereareiam.socialismus.model.phrase.Phrase;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.service.condition.ConditionTexts;
import me.whereareiam.socialismus.service.phrase.PhraseScope;
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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MessagingConditionsTest {
	private final ConditionTexts conditionTexts = mock(ConditionTexts.class);
	private final SocialismusPlayer alice = TestDataBuilder.mockPlayer("Alice");
	private final SocialismusPlayer bob = TestDataBuilder.mockPlayer("Bob");
	private final DialogueSettings settings = TestDataBuilder.defaultSettings();

	@BeforeEach
	void setUp() {
		settings.getMessaging().setRequires(ConditionLists.permission("dialogue.send"));
		settings.getMessaging().setReaches(ConditionLists.permission("dialogue.receive"));
		when(alice.hasPermission("dialogue.send")).thenReturn(true);
	}

	@Test
	void allowsWhatBothParticipantsMeet() {
		when(bob.hasPermission("dialogue.receive")).thenReturn(true);

		assertTrue(conditions().judge(alice, bob, "Bob").allowed());
	}

	@Test
	void wordsARefusedRecipientInDialoguesScope() {
		Phrase reason = Phrase.of("Bob may not receive", Map.of());
		when(conditionTexts.denied(any(), inDialoguesScope(), eq(Map.of("recipientName", "Bob")))).thenReturn(Optional.of(reason));

		MessagingConditions.Judgement verdict = conditions().judge(alice, bob, "Bob");

		assertFalse(verdict.allowed());
		assertSame(reason, verdict.reason());
		assertEquals("messaging.reaches[0].permission", refusal().path());
	}

	@Test
	void judgesTheSenderBeforeTheRecipient() {
		when(alice.hasPermission("dialogue.send")).thenReturn(false);

		assertFalse(conditions().judge(alice, bob, "Bob").allowed());

		Verdict.Failed failed = refusal();
		assertEquals("messaging.requires[0].permission", failed.path());
		assertTrue(failed.about(ConditionParts.SENDER));
		verify(conditionTexts).denied(eq(failed), inDialoguesScope(), eq(Map.of("recipientName", "Bob")));
		verify(bob, never()).hasPermission("dialogue.receive");
	}

	@Test
	void refusesARecipientThisServerDoesNotKnowWithoutAReason() {
		MessagingConditions.Judgement verdict = conditions().judge(alice, null, "Bob");

		assertFalse(verdict.allowed());
		assertNull(verdict.reason());
	}

	@Test
	void allowsARecipientThisServerDoesNotKnowWhenAnyoneMayReceive() {
		settings.getMessaging().setReaches(me.whereareiam.conditia.model.Requirements.none());

		assertTrue(conditions().judge(alice, null, "Bob").allowed());
	}

	private Verdict.Failed refusal() {
		ArgumentCaptor<Verdict.Failed> failed = ArgumentCaptor.forClass(Verdict.Failed.class);
		verify(conditionTexts).denied(failed.capture(), any(), any());

		return failed.getValue();
	}

	private static PhraseScope inDialoguesScope() {
		return argThat(scope -> scope.name().equals("dialogue"));
	}

	private MessagingConditions conditions() {
		return new MessagingConditions(TestDialogueConditions.bound(settings), conditionTexts);
	}
}
