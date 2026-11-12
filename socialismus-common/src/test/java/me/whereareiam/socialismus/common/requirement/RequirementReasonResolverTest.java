package me.whereareiam.socialismus.common.requirement;

import me.whereareiam.conditia.result.Reason;
import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.common.phrase.DefaultPhraseService;
import me.whereareiam.socialismus.model.phrase.Phrases;
import me.whereareiam.socialismus.model.requirement.RequirementParts;
import me.whereareiam.socialismus.model.requirement.RequirementReason;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequirementReasonResolverTest {
	private static final Verdict.Failed OUTSIDE = outside(Map.of());

	private Phrases phrases = Phrases.builder()
			.section("denied", denied -> denied
					.text("world.outside", "Not in {world}")
					.text("recipient.world.outside", "{recipientName} is not here"))
			.section("chat", chat -> chat
					.text("staff-only", "Only staff write in {chat}.")
					.section("denied", denied -> denied.text("world.outside", "{chat} is not for {world}")))
			.build();
	private final RequirementReasonResolver resolver = new RequirementReasonResolver(new DefaultPhraseService(() -> phrases));

	@Test
	void wordsAReasonWithThePhraseOfTheFeaturesScopeFirst() {
		RequirementReason reason = resolver.resolve(OUTSIDE, "chat", Map.of("chat", "local")).orElseThrow();

		assertEquals("{chat} is not for {world}", reason.getMessage());
		assertEquals(Map.of("chat", "local", "world", "world_nether"), reason.getPlaceholders());
	}

	@Test
	void wordsAReasonTheScopeLeavesWithTheRootPhrase() {
		assertEquals("Not in {world}", resolver.resolve(OUTSIDE, "dialogue", Map.of()).orElseThrow().getMessage());
	}

	@Test
	void wordsAReasonAboutAPartWithThePartsPhraseFirst() {
		Verdict.Failed recipient = new Verdict.Failed("messaging.reaches[0].world", "world", List.of(RequirementParts.RECIPIENT),
				Optional.of(new Outside("world_nether")));

		assertEquals("{recipientName} is not here", resolver.resolve(recipient, "", Map.of()).orElseThrow().getMessage());
	}

	@Test
	void prefersTheLinesDeniedText_AndRendersItsReferences() {
		Verdict.Failed failed = outside(Map.of("denied", "<red><ref:staff-only>"));

		assertEquals("<red>Only staff write in {chat}.", resolver.resolve(failed, "chat", Map.of()).orElseThrow().getMessage());
	}

	@Test
	void addsTheLinesHint_AfterWhyItDenied() {
		Verdict.Failed failed = outside(Map.of("hint", "Use /spawn."));

		assertEquals("{chat} is not for {world}\nUse /spawn.", resolver.resolve(failed, "chat", Map.of()).orElseThrow().getMessage());
	}

	@Test
	void wordsNothingWithoutAReasonOrAText() {
		Verdict.Failed any = new Verdict.Failed("chats.local.requires[0].any", "any", List.of(), Optional.empty());
		phrases = Phrases.builder().section("denied", denied -> denied.text("world.outside", " ")).build();

		assertTrue(resolver.resolve(any, "chat", Map.of()).isEmpty());
		assertTrue(resolver.resolve(OUTSIDE, "", Map.of()).isEmpty());
	}

	private static Verdict.Failed outside(Map<String, String> wording) {
		return new Verdict.Failed("chats.local.requires[0].world", "world", List.of(RequirementParts.SENDER),
				Optional.of(new Outside("world_nether")), wording);
	}

	private record Outside(String world) implements Reason {
		@Override
		public @NotNull Map<String, String> values() {
			return Map.of("world", world);
		}
	}
}
