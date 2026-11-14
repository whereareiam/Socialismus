package me.whereareiam.socialismus.common.condition;

import me.whereareiam.conditia.result.Reason;
import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.common.phrase.DefaultPhrasebook;
import me.whereareiam.socialismus.model.condition.ConditionParts;
import me.whereareiam.socialismus.model.phrase.Phrase;
import me.whereareiam.socialismus.model.phrase.Phrases;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultConditionTextsTest {
	private static final Verdict.Failed OUTSIDE = outside(Map.of());

	private Phrases phrases = Phrases.builder()
			.section("denied", denied -> denied
					.text("world.outside", "Not in {world}")
					.text("recipient.world.outside", "{recipientName} is not here"))
			.section("chat", chat -> chat
					.text("staff-only", "Only staff write in {chat}.")
					.section("denied", denied -> denied.text("world.outside", "{chat} is not for {world}")))
			.build();
	private final DefaultPhrasebook phrasebook = new DefaultPhrasebook(() -> phrases);
	private final DefaultConditionTexts texts = new DefaultConditionTexts(phrasebook);

	@Test
	void wordsAReasonWithThePhraseOfTheFeaturesScopeFirst() {
		Phrase reason = texts.denied(OUTSIDE, phrasebook.scope("chat"), Map.of("chat", "local")).orElseThrow();

		assertEquals("{chat} is not for {world}", reason.getMessage());
		assertEquals(Map.of("chat", "local", "world", "world_nether"), reason.getPlaceholders());
	}

	@Test
	void wordsAReasonTheScopeLeavesWithTheRootPhrase() {
		assertEquals("Not in {world}", texts.denied(OUTSIDE, phrasebook.scope("dialogue"), Map.of()).orElseThrow().getMessage());
	}

	@Test
	void wordsAReasonAboutAPartWithThePartsPhraseFirst() {
		Verdict.Failed recipient = new Verdict.Failed("messaging.reaches[0].world", "world", List.of(ConditionParts.RECIPIENT),
				Optional.of(new Outside("world_nether")));

		assertEquals("{recipientName} is not here", texts.denied(recipient, phrasebook.scope(""), Map.of()).orElseThrow().getMessage());
	}

	@Test
	void prefersTheLinesDeniedText_AndRendersItsReferences() {
		Verdict.Failed failed = outside(Map.of("denied", "<red><ref:staff-only>"));

		assertEquals("<red>Only staff write in {chat}.", texts.denied(failed, phrasebook.scope("chat"), Map.of()).orElseThrow().getMessage());
	}

	@Test
	void addsTheLinesHint_AfterWhyItDenied() {
		Verdict.Failed failed = outside(Map.of("hint", "Use /spawn."));

		assertEquals("{chat} is not for {world}\nUse /spawn.", texts.denied(failed, phrasebook.scope("chat"), Map.of()).orElseThrow().getMessage());
	}

	@Test
	void wordsNothingWithoutAReasonOrAText() {
		Verdict.Failed any = new Verdict.Failed("chats.local.requires[0].any", "any", List.of(), Optional.empty());
		phrases = Phrases.builder().section("denied", denied -> denied.text("world.outside", " ")).build();

		assertTrue(texts.denied(any, phrasebook.scope("chat"), Map.of()).isEmpty());
		assertTrue(texts.denied(OUTSIDE, phrasebook.scope(""), Map.of()).isEmpty());
	}

	private static Verdict.Failed outside(Map<String, String> wording) {
		return new Verdict.Failed("chats.local.requires[0].world", "world", List.of(ConditionParts.SENDER),
				Optional.of(new Outside("world_nether")), wording);
	}

	private record Outside(String world) implements Reason {
		@Override
		public @NotNull Map<String, String> values() {
			return Map.of("world", world);
		}
	}
}
