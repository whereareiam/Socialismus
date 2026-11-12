package me.whereareiam.socialismus.common.requirement;

import me.whereareiam.conditia.result.Reason;
import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.model.config.message.Messages;
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
	private static final Verdict.Failed OUTSIDE = new Verdict.Failed(
			"chats.local.requires[0].world", "world", List.of(RequirementParts.SENDER), Optional.of(new Outside("world_nether"))
	);

	private final Messages messages = new Messages();
	private final RequirementReasonResolver resolver = new RequirementReasonResolver(() -> messages);

	@Test
	void wordsAReasonWithTheFeaturesTextFirst() {
		messages.setRequirements(Map.of("world.outside", "core"));

		RequirementReason reason = resolver.resolve(OUTSIDE, Map.of("world.outside", "{chat} is not for {world}"), Map.of("chat", "local"))
				.orElseThrow();

		assertEquals("{chat} is not for {world}", reason.getMessage());
		assertEquals(Map.of("chat", "local", "world", "world_nether"), reason.getPlaceholders());
	}

	@Test
	void wordsAReasonTheFeatureLeavesWithTheTextOfMessages() {
		messages.setRequirements(Map.of("world.outside", "Not in {world}"));

		assertEquals("Not in {world}", resolver.resolve(OUTSIDE, Map.of(), Map.of()).orElseThrow().getMessage());
	}

	@Test
	void wordsNothingWithoutAReasonOrAText() {
		Verdict.Failed any = new Verdict.Failed("chats.local.requires[0].any", "any", List.of(), Optional.empty());

		assertTrue(resolver.resolve(any, Map.of("world.outside", "x"), Map.of()).isEmpty());
		assertTrue(resolver.resolve(OUTSIDE, Map.of("world.outside", " "), Map.of()).isEmpty());
	}

	private record Outside(String world) implements Reason {
		@Override
		public @NotNull Map<String, String> values() {
			return Map.of("world", world);
		}
	}
}
