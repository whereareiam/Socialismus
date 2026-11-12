package me.whereareiam.socialismus.common.phrase;

import me.whereareiam.conditia.Place;
import me.whereareiam.conditia.exception.ConditionException;
import me.whereareiam.socialismus.model.phrase.Phrase;
import me.whereareiam.socialismus.model.phrase.Phrases;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DefaultPhrasebookTest {
	private final DefaultPhrasebook phrases = new DefaultPhrasebook(() -> Phrases.builder()
			.text("contact", "Ask <ref:staff>.")
			.text("staff", "a moderator")
			.text("loop", "<ref:loop> again")
			.section("chat", chat -> chat.text("staff-only", "Only staff. <ref:contact>"))
			.build());

	@Test
	void findsAKeyInTheScopeFirst_ThenWithoutIt() {
		assertEquals(Optional.of("Only staff. <ref:contact>"), phrases.find("chat", "staff-only"));
		assertEquals(Optional.of("a moderator"), phrases.find("chat", "staff"));
		assertEquals(Optional.empty(), phrases.find("", "staff-only"));
	}

	@Test
	void rendersReferences_AndTheReferencesInThem() {
		assertEquals("<red>Only staff. Ask a moderator.", phrases.render("chat", "<red><ref:staff-only>"));
	}

	@Test
	void givesAPhraseReadyToSend_WithItsReferencesAndPlaceholders() {
		Phrase phrase = phrases.phrase("chat", "staff-only", Map.of("chat", "staff")).orElseThrow();

		assertEquals("Only staff. Ask a moderator.", phrase.getMessage());
		assertEquals(Map.of("chat", "staff"), phrase.getPlaceholders());
		assertEquals(Optional.empty(), phrases.phrase("", "missing", Map.of()));
	}

	@Test
	void leavesOutAReferenceNothingResolves_OrOneThatRefersBackToItself() {
		assertEquals("[] [ again]", phrases.render("", "[<ref:missing>] [<ref:loop>]"));
		assertEquals(List.of("missing", "loop"), phrases.unresolved("", "<ref:missing> <ref:loop>"));
		assertEquals(Map.of("loop", List.of("loop")), phrases.unresolved());
	}

	@Test
	void findsTheModulesPhrases_AfterSocialismussOwn_UntilTheModuleStops() {
		phrases.add("dialogue", () -> Phrases.builder().text("staff", "an admin").text("dialogue-only", "Dialogue").build());

		assertEquals(Optional.of("a moderator"), phrases.find("", "staff"));
		assertEquals(Optional.of("Dialogue"), phrases.find("", "dialogue-only"));

		phrases.remove("dialogue");
		assertEquals(Optional.empty(), phrases.find("", "dialogue-only"));
	}

	@Test
	void refusesAPlaceWhoseTextsReferToNoPhrase_NamingTheTextsPath() {
		Place place = mock(Place.class);
		SequencedMap<String, String> wording = new LinkedHashMap<>();
		wording.put("chats.staff.requires[0].denied", "<ref:staff-only>");
		wording.put("chats.staff.requires[1].hint", "<ref:staf-only>");
		when(place.wording()).thenReturn(wording);

		ConditionException exception = assertThrows(ConditionException.class, () -> phrases.checked("chat", place));

		assertEquals("chats.staff.requires[1].hint", exception.getPath());
		verify(place).close();
	}
}
