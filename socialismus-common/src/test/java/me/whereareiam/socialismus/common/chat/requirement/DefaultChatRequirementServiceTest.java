package me.whereareiam.socialismus.common.chat.requirement;

import me.whereareiam.conditia.ConditionScope;
import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.adapter.configura.ConditiaFeature;
import me.whereareiam.conditia.exception.ConditionException;
import me.whereareiam.conditia.model.NoSettings;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.type.Format;
import me.whereareiam.socialismus.common.config.defaults.chat.ChatSettingsDefaults;
import me.whereareiam.socialismus.common.phrase.DefaultPhrasebook;
import me.whereareiam.socialismus.common.requirement.RequirementConfiguration;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatFormat;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.phrase.Phrases;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.RequirementLists;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import static me.whereareiam.conditia.testing.TestRequirements.yaml;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultChatRequirementServiceTest {
	private final SocialismusPlayer alice = mock(SocialismusPlayer.class);
	private final SocialismusPlayer bob = mock(SocialismusPlayer.class);
	private final ChatSettings settings = new ChatSettingsDefaults().supply(new ChatSettings());

	private Phrases phrases = Phrases.none();
	private Conditions conditions;
	private DefaultChatRequirementService requirements;

	@BeforeEach
	void setUp() {
		Logger.init(mock(LoggingHelper.class));
		conditions = new RequirementConfiguration().provideConditions(
				Config.builder().format(Format.YAML).feature(new ConditiaFeature()).build(),
				Set::of,
				() -> mock(PlayerRegistry.class)
		);
		requirements = new DefaultChatRequirementService(conditions, new DefaultPhrasebook(() -> phrases), () -> settings, mock(EventManager.class));
		when(alice.hasPermission("socialismus.chat.local")).thenReturn(true);
	}

	@Test
	void aChatWaitsForTheRequirementsPhase_SoItMayNameTheTypeOfAModule() {
		requirements.bind(chat("staff", "- shop:customer:", "[]"));
		ConditionScope shop = conditions.scope("shop");
		shop.register(new Customer());

		requirements.start();

		assertTrue(requirements.writes(chat("staff", "[]", "[]"), message(alice)).passed());
	}

	@Test
	void aChatThatNamesAnUnknownTypeStopsTheStartWithItsPath() {
		requirements.bind(chat("staff", "- shop:customer:", "[]"));

		ConditionException exception = assertThrows(ConditionException.class, requirements::start);

		assertEquals("chats.staff.requires[0].shop:customer", exception.getPath());
	}

	@Test
	void aChatAddedLaterIsBoundAtOnce_AndRefusedWhenItCannotBe() {
		requirements.start();

		assertThrows(ConditionException.class, () -> requirements.bind(chat("staff", "- nothing: here", "[]")));
		assertInstanceOf(Verdict.Broken.class, requirements.writes(chat("staff", "[]", "[]"), message(alice)));
	}

	@Test
	void checksWhoWritesAndWhomAChatReaches() {
		Chat local = chat("local", "- permission: socialismus.chat.local", "- permission: socialismus.chat.local");
		requirements.bind(local);
		requirements.start();

		assertTrue(requirements.writes(local, message(alice)).passed());
		Verdict.Failed refused = assertInstanceOf(Verdict.Failed.class, requirements.writes(local, message(bob)));
		assertEquals("permission.missing", refused.key().orElseThrow());
		assertTrue(requirements.reaches(local, alice, message(bob)).passed());
		assertFalse(requirements.reaches(local, bob, message(alice)).passed());
	}

	@Test
	void aChatReachesWhoSharesTheSendersServerAndSeesThem_AndTellsWhyNot() {
		Chat staff = chat("staff", "[]", """
				- same: server
				- sees: [recipient, sender]
				""");
		when(alice.getServer()).thenReturn("lobby");
		when(bob.getServer()).thenReturn("lobby");
		when(alice.canSee(bob)).thenReturn(true);
		requirements.bind(staff);
		requirements.start();

		assertTrue(requirements.reaches(staff, alice, message(bob)).passed());

		when(alice.canSee(bob)).thenReturn(false);
		Verdict.Failed hidden = assertInstanceOf(Verdict.Failed.class, requirements.reaches(staff, alice, message(bob)));
		assertEquals("sees.hidden", hidden.key().orElseThrow());

		when(bob.getServer()).thenReturn("survival");
		Verdict.Failed elsewhere = assertInstanceOf(Verdict.Failed.class, requirements.reaches(staff, alice, message(bob)));
		assertEquals("same.outside", elsewhere.key().orElseThrow());
	}

	@Test
	void aChatReachesThePlayersOfItsLanguages() {
		Chat deutsch = chat("deutsch", "[]", "- language: de");
		when(alice.getLocale()).thenReturn(Locale.GERMANY);
		when(bob.getLocale()).thenReturn(Locale.US);
		requirements.bind(deutsch);
		requirements.start();

		assertTrue(requirements.reaches(deutsch, alice, message(bob)).passed());
		assertFalse(requirements.reaches(deutsch, bob, message(alice)).passed());
	}

	@Test
	void sameNamesNeitherAPlaceNorAPlaceholder_AndStopsTheStart() {
		requirements.bind(chat("staff", "[]", "- same: { }"));

		ConditionException exception = assertThrows(ConditionException.class, requirements::start);

		assertEquals("chats.staff.reaches[0].same", exception.getPath());
	}

	@Test
	void nearbyAndWorldAreUnknownWhereNoPositionsAre() {
		requirements.bind(chat("local", "[]", "- nearby: 200"));

		ConditionException exception = assertThrows(ConditionException.class, requirements::start);

		assertEquals("chats.local.reaches[0].nearby", exception.getPath());
	}

	@Test
	void choosesTheLastFormatTheSenderMeets() {
		ChatFormat plain = new ChatFormat("plain", Requirements.none());
		ChatFormat local = new ChatFormat("local", RequirementLists.permission("socialismus.chat.local"));
		Chat chat = Chat.builder().id("local").formats(List.of(plain, local)).build();
		requirements.bind(chat);
		requirements.start();

		assertEquals(local, requirements.format(chat, message(alice)).orElseThrow());
		assertEquals(plain, requirements.format(chat, message(bob)).orElseThrow());
	}

	@Test
	void keepsTheSettingsAsTheyWereWhenAReloadedListCannotBeBound() {
		when(alice.hasPermission("socialismus.admin")).thenReturn(true);
		requirements.start();
		settings.getHistory().setRequires(yaml("- nothing: here"));

		requirements.onReloaded(new PluginReloadedEvent());

		assertTrue(requirements.clears(alice));
		assertFalse(requirements.clears(bob));
	}

	private static Chat chat(String id, String requires, String reaches) {
		return Chat.builder().id(id).requires(yaml(requires)).reaches(yaml(reaches)).formats(List.of()).build();
	}

	private static ChatMessage message(SocialismusPlayer sender) {
		return ChatMessage.builder().id(1).sender(sender).build();
	}

	// A module's type about no part, as a shop module would register it.
	private static final class Customer implements ConditionType.Plain<NoSettings> {
		public @NotNull String name() {
			return "customer";
		}

		public @NotNull Class<NoSettings> settings() {
			return NoSettings.class;
		}

		public @NotNull Outcome holds(@NotNull NoSettings settings) {
			return Outcome.pass();
		}
	}
}
