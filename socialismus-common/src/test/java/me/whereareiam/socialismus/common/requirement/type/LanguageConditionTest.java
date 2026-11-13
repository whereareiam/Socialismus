package me.whereareiam.socialismus.common.requirement.type;

import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LanguageConditionTest {
	private final SocialismusPlayer player = mock(SocialismusPlayer.class);
	private final LanguageCondition language = new LanguageCondition();

	@Test
	void language_TakesEveryCountryOfALanguage_OrOnlyTheCountryNamed() {
		when(player.getLocale()).thenReturn(Locale.of("pt", "BR"));

		assertTrue(language.holds(player, new LanguageCondition.Settings(List.of("pt"))).passed());
		assertTrue(language.holds(player, new LanguageCondition.Settings(List.of("PT-br"))).passed());
		assertFalse(language.holds(player, new LanguageCondition.Settings(List.of("pt_pt"))).passed());
	}

	@Test
	void language_TellsTheLanguagesListedAndThePlayers() {
		when(player.getLocale()).thenReturn(Locale.US);

		Outcome outside = language.holds(player, new LanguageCondition.Settings(List.of("de", "fr")));

		assertEquals(Map.of("languages", "de, fr", "language", "en_us"), outside.reason().orElseThrow().values());
	}
}
