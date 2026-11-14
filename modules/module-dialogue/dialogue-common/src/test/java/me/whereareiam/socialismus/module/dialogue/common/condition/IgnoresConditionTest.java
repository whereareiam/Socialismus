package me.whereareiam.socialismus.module.dialogue.common.condition;

import me.whereareiam.conditia.model.NoSettings;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.IgnoreStore;
import org.junit.jupiter.api.Test;

import static me.whereareiam.socialismus.module.dialogue.common.TestDataBuilder.mockPlayer;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IgnoresConditionTest {
	private final IgnoreStore ignores = mock(IgnoreStore.class);
	private final DialogueConditions conditions = mock(DialogueConditions.class);
	private final IgnoresCondition condition = new IgnoresCondition(ignores, conditions);
	private final SocialismusPlayer steve = mockPlayer("Steve");
	private final SocialismusPlayer alex = mockPlayer("Alex");

	@Test
	void holdsWhenThePlayerIgnoresTheOther() {
		when(ignores.ignores(steve.getUniqueId(), alex.getUniqueId())).thenReturn(true);

		assertTrue(condition.holds(steve, alex, NoSettings.INSTANCE).passed());
		assertFalse(condition.holds(alex, steve, NoSettings.INSTANCE).passed());
	}

	@Test
	void nobodyIgnoresAPlayerExemptFromIgnoring() {
		when(ignores.ignores(steve.getUniqueId(), alex.getUniqueId())).thenReturn(true);
		when(conditions.exemptFromIgnore(alex)).thenReturn(true);

		assertFalse(condition.holds(steve, alex, NoSettings.INSTANCE).passed());
	}
}
