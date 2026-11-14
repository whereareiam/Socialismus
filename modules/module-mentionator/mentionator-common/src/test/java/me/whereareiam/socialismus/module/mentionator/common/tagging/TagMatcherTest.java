package me.whereareiam.socialismus.module.mentionator.common.tagging;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.Tag;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.TagParameters;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static me.whereareiam.socialismus.module.mentionator.common.TestMentionator.player;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TagMatcherTest {
	private static final Tag PLAYER = Tag.builder()
			.id("player")
			.written(List.of("@{username}", "{username}"))
			.parameters(TagParameters.builder().minimumLength(3).build())
			.build();
	private static final Tag EVERYONE = Tag.builder().id("everyone").priority(10).written(List.of("@everyone", "@all")).build();
	private static final Tag RANK = Tag.builder().id("rank").written(List.of("@%luckperms_primary_group_name%")).build();

	private final SocialismusPlayer steve = player("Steve");
	private final SocialismusPlayer al = player("Al");
	private final SocialismusPlayer alice = player("Alice");
	private final List<Candidate> candidates = List.of(
			new Candidate(steve, Map.of("%luckperms_primary_group_name%", "Admin")),
			new Candidate(al, Map.of()),
			new Candidate(alice, Map.of("%luckperms_primary_group_name%", "admin"))
	);

	@Test
	void aNameIsFoundIgnoringCaseAndKeepsItsOwnSpelling() {
		List<Found> found = find("hey @sTeVe, look");

		assertEquals(1, found.size());
		assertEquals("Steve", found.getFirst().match());
		assertEquals(4, found.getFirst().start());
		assertEquals(10, found.getFirst().end());
		assertEquals(List.of(steve), players(found.getFirst()));
	}

	@Test
	void onlyWholeWordsAreTags() {
		assertTrue(find("@Steves and Stevenson and mail@Steve").isEmpty());
	}

	@Test
	void punctuationAroundATagIsFine() {
		assertEquals(List.of("Steve", "Alice"), matches(find("(@Steve) and Alice!")));
	}

	@Test
	void aBareNameMustBeAsLongAsTheTagAsks() {
		List<Found> found = find("al is here, @Al too");

		assertEquals(List.of("Al"), matches(found));
		assertEquals(12, found.getFirst().start());
	}

	@Test
	void aLongerNameWinsOverItsBeginning() {
		assertEquals(List.of("Alice"), matches(find("@Alice")));
	}

	@Test
	void aTagOfHigherPriorityWinsWhereSeveralFit() {
		Tag all = Tag.builder().id("player-all").written(List.of("@all")).build();

		List<Found> found = TagMatcher.find("@all", List.of(all, EVERYONE), candidates);

		assertEquals("everyone", found.getFirst().tag().getId());
	}

	@Test
	void aLiteralTagNamesEveryCandidate() {
		Found everyone = find("@EVERYONE look").getFirst();

		assertEquals("@everyone", everyone.match());
		assertEquals(3, everyone.candidates().size());
	}

	@Test
	void aPlaceholderValueNamesEveryPlayerWithItIgnoringCase() {
		Found admins = find("ping @admin").getFirst();

		assertEquals("rank", admins.tag().getId());
		assertEquals(List.of(steve, alice), players(admins));
	}

	@Test
	void aBackslashEscapesATag() {
		Found escaped = find("say \\@everyone please").getFirst();

		assertTrue(escaped.escaped());
		assertEquals(5, escaped.start());
	}

	@Test
	void aNameNobodyOnlineHasIsNoTag() {
		assertTrue(find("@Herobrine").isEmpty());
	}

	private List<Found> find(String text) {
		return TagMatcher.find(text, List.of(PLAYER, EVERYONE, RANK), candidates);
	}

	private static List<String> matches(List<Found> found) {
		return found.stream().map(Found::match).toList();
	}

	private static List<SocialismusPlayer> players(Found found) {
		return found.candidates().stream().map(Candidate::player).toList();
	}
}
