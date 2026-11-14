package me.whereareiam.socialismus.common.style;

import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.adapter.configura.ConditiaFeature;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.type.Format;
import me.whereareiam.socialismus.common.condition.ConditionConfiguration;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.exception.StyleException;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.condition.ConditionLists;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.style.Style;
import me.whereareiam.socialismus.model.style.StylesConfig;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.service.style.StylePlace;
import me.whereareiam.socialismus.service.style.TextStyle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultStylesTest {
	private static final List<String> LADDER = List.of("basic", "vip", "staff");

	private final SocialismusPlayer alice = mock(SocialismusPlayer.class);
	private final SocialismusPlayer bob = mock(SocialismusPlayer.class);
	private final SocialismusPlayer carol = mock(SocialismusPlayer.class);
	private final SocialismusPlayer dave = mock(SocialismusPlayer.class);
	private final StylesConfig file = new StylesConfig();
	private final Settings settings = new Settings();

	private DefaultStyles styles;

	@BeforeEach
	void setUp() {
		Logger.init(mock(LoggingHelper.class));
		settings.setSerialization(new Settings.Serialization());
		Conditions conditions = new ConditionConfiguration().provideConditions(
				Config.builder().format(Format.YAML).feature(new ConditiaFeature()).build(),
				Set::of,
				() -> mock(PlayerRegistry.class)
		);
		file.setStyles(new ArrayList<>(List.of(
				style("basic", List.of("color", "bold", "italic"), List.of()),
				style("vip", List.of("*"), List.of("obfuscated", "font")),
				style("staff", List.of("*"), List.of())
		)));
		styles = new DefaultStyles(conditions, () -> file, () -> settings, mock(EventManager.class));

		when(alice.hasPermission("socialismus.style.basic")).thenReturn(true);
		when(bob.hasPermission("socialismus.style.basic")).thenReturn(true);
		when(bob.hasPermission("socialismus.style.vip")).thenReturn(true);
		when(carol.hasPermission("socialismus.style.vip")).thenReturn(true);
		when(carol.hasPermission("socialismus.style.staff")).thenReturn(true);
	}

	@Test
	void aWriterGetsTheLastStyleOfThePlaceTheyMeet_LikeTheFormatsOfAChat() {
		StylePlace place = styles.place("chats.global.parameters.styles", LADDER);
		styles.start();

		assertEquals("basic", place.of(alice).id());
		assertEquals("vip", place.of(bob).id());
		assertEquals("staff", place.of(carol).id());
		assertSame(TextStyle.PLAIN, place.of(dave));
		assertSame(TextStyle.PLAIN, styles.place("chats.local.parameters.styles", List.of()).of(carol));
	}

	@Test
	void readsOnlyTheTagsOfTheStyle_AndLeavesTheOthersAsTyped() {
		styles.start();

		Component read = styles.style("basic").read("<bold>all</bold> <rainbow>hi</rainbow>");

		assertEquals("all <rainbow>hi</rainbow>", plain(read));
		assertTrue(decorated(read, TextDecoration.BOLD));
	}

	@Test
	void anExcludedTagStaysText() {
		styles.start();

		assertEquals("<obfuscated>x</obfuscated>", plain(styles.style("vip").read("<obfuscated>x</obfuscated>")));
		assertEquals("x", plain(styles.style("staff").read("<obfuscated>x</obfuscated>")));
	}

	@Test
	void neverReadsTagsThatActOnTheReader_EvenForAStyleOfEverything() {
		styles.start();
		String text = "<click:run_command:'/op carol'>free diamonds</click><insert:/op carol>x</insert><newline><reset>";

		Component read = styles.style("staff").read(text);

		assertEquals(text, plain(read));
		assertFalse(clicks(read));
	}

	@Test
	void neverResolvesPlaceholdersInWhatAPlayerWrote() {
		styles.start();

		assertEquals("%player_ip% {playerName}", plain(styles.style("staff").read("%player_ip% {playerName}")));
	}

	@Test
	void aStyleNoOneDefinesReadsAsPlainText_AsBeforeTheStylesAreBound() {
		assertSame(TextStyle.PLAIN, styles.style("basic"));

		styles.start();

		assertSame(TextStyle.PLAIN, styles.style("defined-on-another-server"));
		assertEquals(Component.text("<bold>hi"), TextStyle.PLAIN.read("<bold>hi"));
	}

	@Test
	void legacyCodesWorkOnlyAsTheTagsTheStyleAllows() {
		settings.getSerialization().setEnableLegacyColors(true);
		styles.start();

		Component read = styles.style("basic").read("&lloud &kmagic &rreset");

		assertEquals("loud &kmagic &rreset", plain(read));
		assertTrue(decorated(read, TextDecoration.BOLD));
	}

	@Test
	void aPlaceThatAcceptsAStyleNoOneDefinesStopsTheStartWithItsPath() {
		styles.place("chats.global.parameters.styles", List.of("basic", "basci"));

		StyleException failure = assertThrows(StyleException.class, styles::start);

		assertEquals("chats.global.parameters.styles[1]", failure.getPath());
	}

	@Test
	void aPlaceBoundOnceTheStylesAreIsCheckedAtOnce() {
		styles.start();

		StyleException failure = assertThrows(StyleException.class, () -> styles.place("bubbles.default.styles", List.of("vipp")));

		assertEquals("bubbles.default.styles[0]", failure.getPath());
	}

	@Test
	void aStyleThatNamesATagNoStyleMayAllowStopsTheStartWithItsPath() {
		file.getStyles().add(style("tricks", List.of("bold", "click"), List.of()));

		StyleException failure = assertThrows(StyleException.class, styles::start);

		assertEquals("styles[3].tags[1]", failure.getPath());
	}

	@Test
	void aStyleThatNamesNoTagStopsTheStartWithItsPath() {
		file.getStyles().add(style("typo", List.of(), List.of("boldd")));

		StyleException failure = assertThrows(StyleException.class, styles::start);

		assertEquals("styles[3].excluded[0]", failure.getPath());
	}

	@Test
	void noStyleIsCalledPlain() {
		file.getStyles().add(style("plain", List.of("bold"), List.of()));

		StyleException failure = assertThrows(StyleException.class, styles::start);

		assertEquals("styles[3]", failure.getPath());
	}

	@Test
	void keepsTheStylesAsTheyWereWhenReloadedOnesCannotBeRead() {
		StylePlace place = styles.place("chats.global.parameters.styles", LADDER);
		styles.start();
		file.getStyles().add(style("broken", List.of("nbt"), List.of()));

		styles.onReloaded(new PluginReloadedEvent());

		assertEquals("basic", place.of(alice).id());
	}

	@Test
	void keepsTheStylesAsTheyWereWhenAPlaceAcceptsOneTheReloadedFileLacks() {
		StylePlace place = styles.place("chats.global.parameters.styles", LADDER);
		styles.start();
		file.getStyles().removeIf(style -> style.getId().equals("vip"));

		styles.onReloaded(new PluginReloadedEvent());

		assertEquals("vip", place.of(bob).id());
	}

	@Test
	void aClosedPlaceIsNoLongerChecked() {
		styles.place("chats.global.parameters.styles", LADDER).close();
		styles.start();
		file.getStyles().removeIf(style -> style.getId().equals("vip"));

		styles.onReloaded(new PluginReloadedEvent());

		assertSame(TextStyle.PLAIN, styles.style("vip"));
	}

	private static Style style(String id, List<String> tags, List<String> excluded) {
		return Style.builder()
				.id(id)
				.tags(tags)
				.excluded(excluded)
				.requires(ConditionLists.permission("socialismus.style." + id))
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

		return component.children().stream().anyMatch(DefaultStylesTest::clicks);
	}
}
