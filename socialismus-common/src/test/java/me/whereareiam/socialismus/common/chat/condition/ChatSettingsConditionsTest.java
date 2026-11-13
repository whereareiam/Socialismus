package me.whereareiam.socialismus.common.chat.condition;

import me.whereareiam.conditia.adapter.configura.ConditiaFeature;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.type.Format;
import me.whereareiam.socialismus.common.condition.ConditionConfiguration;
import me.whereareiam.socialismus.common.config.defaults.chat.ChatSettingsDefaults;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static me.whereareiam.conditia.testing.TestRequirements.yaml;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatSettingsConditionsTest {
	private final SocialismusPlayer alice = mock(SocialismusPlayer.class);
	private final SocialismusPlayer bob = mock(SocialismusPlayer.class);
	private final ChatSettings settings = new ChatSettingsDefaults().supply(new ChatSettings());

	private ChatSettingsConditions settingsConditions;

	@BeforeEach
	void setUp() {
		Logger.init(mock(LoggingHelper.class));
		settingsConditions = new ChatSettingsConditions(
				new ConditionConfiguration().provideConditions(
						Config.builder().format(Format.YAML).feature(new ConditiaFeature()).build(),
						Set::of,
						() -> mock(PlayerRegistry.class)
				),
				() -> settings,
				mock(EventManager.class)
		);
		when(alice.hasPermission("socialismus.admin")).thenReturn(true);
	}

	@Test
	void nobodyMeetsTheSettingsBeforeTheConditionsPhase() {
		assertFalse(settingsConditions.clears(alice));
		assertFalse(settingsConditions.keeps(alice));
		assertFalse(settingsConditions.bypasses(alice));
	}

	@Test
	void keepsTheSettingsAsTheyWereWhenAReloadedListCannotBeBound() {
		settingsConditions.start();
		settings.getHistory().setRequires(yaml("- nothing: here"));

		settingsConditions.onReloaded(new PluginReloadedEvent());

		assertTrue(settingsConditions.clears(alice));
		assertFalse(settingsConditions.clears(bob));
	}
}
