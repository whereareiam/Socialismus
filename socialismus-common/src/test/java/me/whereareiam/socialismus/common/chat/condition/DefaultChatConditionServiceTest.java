package me.whereareiam.socialismus.common.chat.condition;

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
import me.whereareiam.socialismus.common.condition.ConditionConfiguration;
import me.whereareiam.socialismus.common.config.SerializationServiceAdapter;
import me.whereareiam.socialismus.common.config.defaults.chat.ChatSettingsDefaults;
import me.whereareiam.socialismus.common.phrase.DefaultPhrasebook;
import me.whereareiam.socialismus.common.store.DefaultStores;
import me.whereareiam.socialismus.common.style.TestStyles;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatFormat;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.condition.ConditionLists;
import me.whereareiam.socialismus.model.phrase.Phrases;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static me.whereareiam.conditia.testing.TestRequirements.yaml;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultChatConditionServiceTest {
	private final SocialismusPlayer alice = mock(SocialismusPlayer.class);
	private final SocialismusPlayer bob = mock(SocialismusPlayer.class);
	private final ChatSettings settings = new ChatSettingsDefaults().supply(new ChatSettings());

	private Phrases phrases = Phrases.none();
	private Conditions conditions;
	private DefaultChatConditionService chatConditions;

	@BeforeEach
	void setUp() {
		Logger.init(mock(LoggingHelper.class));
		conditions = new ConditionConfiguration().provideConditions(
				Config.builder().format(Format.YAML).feature(new ConditiaFeature()).build(),
				Set::of,
				() -> mock(PlayerRegistry.class),
				new DefaultStores(new SerializationServiceAdapter())
		);
		chatConditions = new DefaultChatConditionService(conditions, new DefaultPhrasebook(() -> phrases), new TestStyles(), () -> settings, mock(EventManager.class));
		when(alice.hasPermission("socialismus.chat.local")).thenReturn(true);
	}

	@Test
	void aChatWaitsForTheConditionsPhase_SoItMayNameTheTypeOfAModule() {
		chatConditions.bind(chat("staff", "- shop:customer:", "[]"));
		ConditionScope shop = conditions.scope("shop");
		shop.register(new Customer());

		chatConditions.start();

		assertTrue(chatConditions.writes(chat("staff", "[]", "[]"), message(alice)).passed());
	}

	@Test
	void aChatThatNamesAnUnknownTypeStopsTheStartWithItsPath() {
		chatConditions.bind(chat("staff", "- shop:customer:", "[]"));

		ConditionException exception = assertThrows(ConditionException.class, chatConditions::start);

		assertEquals("chats.staff.requires[0].shop:customer", exception.getPath());
	}

	@Test
	void aChatAddedLaterIsBoundAtOnce_AndRefusedWhenItCannotBe() {
		chatConditions.start();

		assertThrows(ConditionException.class, () -> chatConditions.bind(chat("staff", "- nothing: here", "[]")));
		assertInstanceOf(Verdict.Broken.class, chatConditions.writes(chat("staff", "[]", "[]"), message(alice)));
	}

	@Test
	void checksWhoWritesAndWhomAChatReaches() {
		Chat local = chat("local", "- permission: socialismus.chat.local", "- permission: socialismus.chat.local");
		chatConditions.bind(local);
		chatConditions.start();

		assertTrue(chatConditions.writes(local, message(alice)).passed());
		Verdict.Failed refused = assertInstanceOf(Verdict.Failed.class, chatConditions.writes(local, message(bob)));
		assertEquals("permission.missing", refused.key().orElseThrow());
		assertTrue(chatConditions.reaches(local, alice, message(bob)).passed());
		assertFalse(chatConditions.reaches(local, bob, message(alice)).passed());
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
		chatConditions.bind(staff);
		chatConditions.start();

		assertTrue(chatConditions.reaches(staff, alice, message(bob)).passed());

		when(alice.canSee(bob)).thenReturn(false);
		Verdict.Failed hidden = assertInstanceOf(Verdict.Failed.class, chatConditions.reaches(staff, alice, message(bob)));
		assertEquals("sees.hidden", hidden.key().orElseThrow());

		when(bob.getServer()).thenReturn("survival");
		Verdict.Failed elsewhere = assertInstanceOf(Verdict.Failed.class, chatConditions.reaches(staff, alice, message(bob)));
		assertEquals("same.outside", elsewhere.key().orElseThrow());
	}

	@Test
	void aChatReachesThePlayersOfItsLanguages() {
		Chat deutsch = chat("deutsch", "[]", "- language: de");
		when(alice.getLocale()).thenReturn(Locale.GERMANY);
		when(bob.getLocale()).thenReturn(Locale.US);
		chatConditions.bind(deutsch);
		chatConditions.start();

		assertTrue(chatConditions.reaches(deutsch, alice, message(bob)).passed());
		assertFalse(chatConditions.reaches(deutsch, bob, message(alice)).passed());
	}

	@Test
	void sameNamesNeitherAPlaceNorAPlaceholder_AndStopsTheStart() {
		chatConditions.bind(chat("staff", "[]", "- same: { }"));

		ConditionException exception = assertThrows(ConditionException.class, chatConditions::start);

		assertEquals("chats.staff.reaches[0].same", exception.getPath());
	}

	@Test
	void nearbyAndWorldAreUnknownWhereNoPositionsAre() {
		chatConditions.bind(chat("local", "[]", "- nearby: 200"));

		ConditionException exception = assertThrows(ConditionException.class, chatConditions::start);

		assertEquals("chats.local.reaches[0].nearby", exception.getPath());
	}

	@Test
	void aCooldownCountsOnlyWhatWasRecorded_PerLine_OrPerKeyAcrossLines() {
		when(alice.getUniqueId()).thenReturn(java.util.UUID.randomUUID());
		Chat global = chat("global", "- cooldown: 1m", "[]");
		Chat shout = chat("shout", "- cooldown: { duration: 1m, key: loud }", "[]");
		Chat yell = chat("yell", "- cooldown: { duration: 1m, key: loud }", "[]");
		List.of(global, shout, yell).forEach(chatConditions::bind);
		chatConditions.start();

		Verdict.Passed first = assertInstanceOf(Verdict.Passed.class, chatConditions.writes(global, message(alice)));
		assertTrue(chatConditions.writes(global, message(alice)).passed());

		first.record();

		Verdict.Failed waiting = assertInstanceOf(Verdict.Failed.class, chatConditions.writes(global, message(alice)));
		assertEquals("cooldown.waiting", waiting.key().orElseThrow());
		assertEquals(Map.of("remaining", "1m"), waiting.reason().orElseThrow().values());
		assertTrue(chatConditions.writes(shout, message(alice)).passed());

		((Verdict.Passed) chatConditions.writes(shout, message(alice))).record();

		assertFalse(chatConditions.writes(yell, message(alice)).passed());
	}

	@Test
	void choosesTheLastFormatTheSenderMeets() {
		ChatFormat plain = new ChatFormat("plain", Requirements.none());
		ChatFormat local = new ChatFormat("local", ConditionLists.permission("socialismus.chat.local"));
		Chat chat = Chat.builder().id("local").formats(List.of(plain, local)).build();
		chatConditions.bind(chat);
		chatConditions.start();

		assertEquals(local, chatConditions.format(chat, message(alice)).orElseThrow());
		assertEquals(plain, chatConditions.format(chat, message(bob)).orElseThrow());
	}

	@Test
	void keepsTheSettingsAsTheyWereWhenAReloadedListCannotBeBound() {
		when(alice.hasPermission("socialismus.admin")).thenReturn(true);
		chatConditions.start();
		settings.getHistory().setRequires(yaml("- nothing: here"));

		chatConditions.onReloaded(new PluginReloadedEvent());

		assertTrue(chatConditions.clears(alice));
		assertFalse(chatConditions.clears(bob));
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
