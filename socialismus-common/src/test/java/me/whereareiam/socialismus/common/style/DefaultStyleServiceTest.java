package me.whereareiam.socialismus.common.style;

import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.adapter.configura.ConditiaFeature;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.type.Format;
import me.whereareiam.socialismus.common.requirement.RequirementConfiguration;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.RequirementLists;
import me.whereareiam.socialismus.model.style.Style;
import me.whereareiam.socialismus.model.style.Styles;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultStyleServiceTest {
	private static final List<String> LADDER = List.of("basic", "vip", "staff");

	private final SocialismusPlayer alice = mock(SocialismusPlayer.class);
	private final SocialismusPlayer bob = mock(SocialismusPlayer.class);
	private final SocialismusPlayer carol = mock(SocialismusPlayer.class);
	private final SocialismusPlayer dave = mock(SocialismusPlayer.class);
	private final Styles file = new Styles();
	private final Settings settings = new Settings();

	private DefaultStyleService styles;

	@BeforeEach
	void setUp() {
		Logger.init(mock(LoggingHelper.class));
		settings.setSerialization(new Settings.Serialization());
		Conditions conditions = new RequirementConfiguration().provideConditions(
				Config.builder().format(Format.YAML).feature(new ConditiaFeature()).build(),
				Set::of,
				() -> mock(PlayerRegistry.class)
		);
		file.setStyles(new ArrayList<>(List.of(
				style("basic", List.of("color", "bold", "italic"), List.of()),
				style("vip", List.of("*"), List.of("obfuscated", "font")),
				style("staff", List.of("*"), List.of())
		)));
		styles = new DefaultStyleService(conditions, () -> file, () -> settings, mock(EventManager.class));

		when(alice.hasPermission("socialismus.style.basic")).thenReturn(true);
		when(bob.hasPermission("socialismus.style.basic")).thenReturn(true);
		when(bob.hasPermission("socialismus.style.vip")).thenReturn(true);
		when(carol.hasPermission("socialismus.style.vip")).thenReturn(true);
		when(carol.hasPermission("socialismus.style.staff")).thenReturn(true);
	}

	@Test
	void aWriterGetsTheLastStyleTheyMeet_LikeTheFormatsOfAChat() {
		styles.start();

		assertEquals(Optional.of("basic"), styles.styleOf(alice, LADDER));
		assertEquals(Optional.of("vip"), styles.styleOf(bob, LADDER));
		assertEquals(Optional.of("staff"), styles.styleOf(carol, LADDER));
		assertEquals(Optional.empty(), styles.styleOf(dave, LADDER));
		assertEquals(Optional.empty(), styles.styleOf(carol, List.of()));
	}

	@Test
	void readsOnlyTheTagsOfTheStyle_AndLeavesTheOthersAsTyped() {
		styles.start();

		Component read = styles.read(alice, "<bold>all</bold> <rainbow>hi</rainbow>", LADDER);

		assertEquals("all <rainbow>hi</rainbow>", plain(read));
		assertTrue(decorated(read, TextDecoration.BOLD));
	}

	@Test
	void anExcludedTagStaysText() {
		styles.start();

		assertEquals("<obfuscated>x</obfuscated>", plain(styles.read(bob, "<obfuscated>x</obfuscated>", LADDER)));
		assertEquals("x", plain(styles.read(carol, "<obfuscated>x</obfuscated>", LADDER)));
	}

	@Test
	void neverReadsTagsThatActOnTheReader_EvenForAStyleOfEverything() {
		styles.start();
		String text = "<click:run_command:'/op carol'>free diamonds</click><insert:/op carol>x</insert><newline><reset>";

		Component read = styles.read(carol, text, LADDER);

		assertEquals(text, plain(read));
		assertFalse(clicks(read));
	}

	@Test
	void neverResolvesPlaceholdersInWhatAPlayerWrote() {
		styles.start();

		assertEquals("%player_ip% {playerName}", plain(styles.read(carol, "%player_ip% {playerName}", LADDER)));
	}

	@Test
	void aWriterWithoutAStyleWritesPlainText() {
		styles.start();

		assertEquals(Component.text("<bold>hi"), styles.read(dave, "<bold>hi", LADDER));
		assertEquals(Component.text("<bold>hi"), styles.read("<bold>hi", "unknown"));
	}

	@Test
	void legacyCodesWorkOnlyAsTheTagsTheStyleAllows() {
		settings.getSerialization().setEnableLegacyColors(true);
		styles.start();

		Component read = styles.read(alice, "&lloud &kmagic &rreset", LADDER);

		assertEquals("loud &kmagic &rreset", plain(read));
		assertTrue(decorated(read, TextDecoration.BOLD));
	}

	@Test
	void aStyleThatNamesATagNoStyleMayAllowStopsTheStartWithItsPath() {
		file.getStyles().add(style("tricks", List.of("bold", "click"), List.of()));

		IllegalArgumentException failure = assertThrows(IllegalArgumentException.class, styles::start);

		assertTrue(failure.getMessage().startsWith("styles[3].tags[1]"), failure.getMessage());
	}

	@Test
	void aStyleThatNamesNoTagStopsTheStartWithItsPath() {
		file.getStyles().add(style("typo", List.of(), List.of("boldd")));

		IllegalArgumentException failure = assertThrows(IllegalArgumentException.class, styles::start);

		assertTrue(failure.getMessage().startsWith("styles[3].excluded[0]"), failure.getMessage());
	}

	@Test
	void keepsTheStylesAsTheyWereWhenReloadedOnesCannotBeRead() {
		styles.start();
		file.getStyles().add(style("broken", List.of("nbt"), List.of()));

		styles.onReloaded(new PluginReloadedEvent());

		assertEquals(Optional.of("basic"), styles.styleOf(alice, LADDER));
	}

	private static Style style(String id, List<String> tags, List<String> excluded) {
		return Style.builder()
				.id(id)
				.tags(tags)
				.excluded(excluded)
				.requires(RequirementLists.permission("socialismus.style." + id))
				.build();
	}

	private static String plain(Component component) {
		return PlainTextComponentSerializer.plainText().serialize(component);
	}

	private static boolean decorated(Component component, TextDecoration decoration) {
		if (component.decoration(decoration) == TextDecoration.State.TRUE) return true;

		return component.children().stream().anyMatch(child -> decorated(child, decoration));
	}

	private static boolean clicks(Component component) {
		if (component.clickEvent() != null) return true;

		return component.children().stream().anyMatch(DefaultStyleServiceTest::clicks);
	}
}
