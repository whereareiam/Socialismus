package me.whereareiam.socialismus.common.phrase;

import me.whereareiam.conditia.Place;
import me.whereareiam.conditia.exception.ConditionException;
import me.whereareiam.socialismus.model.phrase.Phrase;
import me.whereareiam.socialismus.model.phrase.Phrases;
import me.whereareiam.socialismus.service.phrase.PhraseScope;
import me.whereareiam.socialismus.service.phrase.PhraseSource;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
	private final PhraseScope chat = phrases.scope("chat");
	private final PhraseScope none = phrases.scope("");

	@Test
	void findsAKeyInTheScopeFirst_ThenWithoutIt() {
		assertEquals(Optional.of("Only staff. <ref:contact>"), chat.find("staff-only"));
		assertEquals(Optional.of("a moderator"), chat.find("staff"));
		assertEquals(Optional.empty(), none.find("staff-only"));
	}

	@Test
	void rendersReferences_AndTheReferencesInThem() {
		assertEquals("<red>Only staff. Ask a moderator.", chat.render("<red><ref:staff-only>"));
	}

	@Test
	void givesAPhraseReadyToSend_WithItsReferencesAndPlaceholders() {
		Phrase phrase = chat.phrase("staff-only", Map.of("chat", "staff")).orElseThrow();

		assertEquals("Only staff. Ask a moderator.", phrase.getMessage());
		assertEquals(Map.of("chat", "staff"), phrase.getPlaceholders());
		assertEquals(Optional.empty(), none.phrase("missing", Map.of()));
	}

	@Test
	void leavesOutAReferenceNothingResolves_OrOneThatRefersBackToItself() {
		assertEquals("[] [ again]", none.render("[<ref:missing>] [<ref:loop>]"));
		assertEquals(List.of("missing", "loop"), none.unresolved("<ref:missing> <ref:loop>"));
		assertEquals(Map.of("loop", List.of("loop")), phrases.unresolved());
	}

	@Test
	void findsTheModulesPhrases_AfterSocialismussOwn_UntilTheModuleStops() {
		PhraseSource source = phrases.add("dialogue", () -> Phrases.builder().text("staff", "an admin").text("dialogue-only", "Dialogue").build());

		assertEquals(Optional.of("a moderator"), none.find("staff"));
		assertEquals(Optional.of("Dialogue"), none.find("dialogue-only"));

		source.close();
		assertEquals(Optional.empty(), none.find("dialogue-only"));
	}

	@Test
	void replacesAModulesPhrasesAddedAgain_SoTheEarlierSourceClosesNothing() {
		PhraseSource earlier = phrases.add("dialogue", () -> Phrases.builder().text("greeting", "Hello").build());
		PhraseSource later = phrases.add("dialogue", () -> Phrases.builder().text("greeting", "Hi").build());

		earlier.close();
		assertEquals(Optional.of("Hi"), none.find("greeting"));

		later.close();
		assertEquals(Optional.empty(), none.find("greeting"));
	}

	@Test
	void namesTheScopeInARefusal_OnlyWhenThereIsOne() {
		Place place = mock(Place.class);
		when(place.wording()).thenReturn(new LinkedHashMap<>(Map.of("chats.staff.requires[0].denied", "<ref:missing>")));

		assertTrue(assertThrows(ConditionException.class, () -> chat.checked(place)).getMessage().contains("no phrase is named missing or chat.missing"));
		assertFalse(assertThrows(ConditionException.class, () -> none.checked(place)).getMessage().contains(" or "));
	}

	@Test
	void refusesAPlaceWhoseTextsReferToNoPhrase_NamingTheTextsPath() {
		Place place = mock(Place.class);
		SequencedMap<String, String> wording = new LinkedHashMap<>();
		wording.put("chats.staff.requires[0].denied", "<ref:staff-only>");
		wording.put("chats.staff.requires[1].hint", "<ref:staf-only>");
		when(place.wording()).thenReturn(wording);

		ConditionException exception = assertThrows(ConditionException.class, () -> chat.checked(place));

		assertEquals("chats.staff.requires[1].hint", exception.getPath());
		verify(place).close();
	}
}
