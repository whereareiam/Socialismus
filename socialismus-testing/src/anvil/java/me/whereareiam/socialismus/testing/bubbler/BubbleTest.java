package me.whereareiam.socialismus.testing.bubbler;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.anvil.capability.entities.model.SeenEntity;
import me.whereareiam.socialismus.testing.environment.MinecraftVersions;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.Network;
import me.whereareiam.socialismus.testing.environment.Plugin;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Console;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bubbler's bubble above a player who chats or runs {@code /socialismus bubble}, as a nearby player sees it, with
 * each render strategy, on Paper and on Spigot. Text displays exist from Minecraft 1.19.4; below it Bubbler warns
 * that they show nothing.
 */
@Tag("bubbler")
class BubbleTest {
	private static final String TEXT_DISPLAY = "minecraft:text_display";
	private static final String ARMOR_STAND = "minecraft:armor_stand";

	@Test
	@Tag("compatibility")
	@MinecraftVersions(since = "1.19.4")
	@Socialismus(plugins = Plugin.PACKETEVENTS, modules = Module.BUBBLER)
	void chatShowsATextDisplayBubbleThatDisappears(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		alice.chat("hello");

		SeenEntity bubble = bob.seesAbove(alice, "hello");
		assertEquals(TEXT_DISPLAY, bubble.getType());
		bob.seesVanish(bubble);
	}

	@Test
	@Tag("compatibility")
	@MinecraftVersions(since = "1.19.4")
	@Socialismus(plugins = Plugin.PACKETEVENTS, modules = Module.BUBBLER)
	void bubbleCommandShowsATextDisplayBubble(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();
		Player bob = Player.joins(anvil, "Bob");

		alice.command("socialismus bubble hello");

		SeenEntity bubble = bob.seesAbove(alice, "hello");
		assertEquals(TEXT_DISPLAY, bubble.getType());
		bob.seesVanish(bubble);
	}

	@Test
	@Tag("compatibility")
	@Socialismus(plugins = Plugin.PACKETEVENTS, modules = Module.BUBBLER, configure = ArmorStands.class)
	void chatShowsAnArmorStandBubbleThatDisappears(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		alice.chat("hello");

		SeenEntity bubble = bob.seesAbove(alice, "hello");
		assertEquals(ARMOR_STAND, bubble.getType());
		bob.seesVanish(bubble);
	}

	@Test
	@Tag("compatibility")
	@Socialismus(plugins = Plugin.PACKETEVENTS, modules = Module.BUBBLER, configure = ArmorStands.class)
	void bubbleCommandShowsAnArmorStandBubble(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();
		Player bob = Player.joins(anvil, "Bob");

		alice.command("socialismus bubble hello");

		SeenEntity bubble = bob.seesAbove(alice, "hello");
		assertEquals(ARMOR_STAND, bubble.getType());
		bob.seesVanish(bubble);
	}

	@Test
	@MinecraftVersions(since = "1.19.4")
	@Socialismus(plugins = Plugin.PACKETEVENTS, modules = Module.BUBBLER)
	void bubbleFollowsTheChatOfTheMessageItself(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		// The default bubble leaves out the global chat.
		alice.chat("!across the world");
		bob.sees("[G] Alice: across the world");
		bob.neverSeesLabelled("across the world");

		alice.chat("next to you");
		bob.seesAbove(alice, "next to you");
	}

	@Test
	@Tag("compatibility")
	@MinecraftVersions(since = "1.19.4")
	@Socialismus(network = Network.SPIGOT, plugins = Plugin.PACKETEVENTS, modules = Module.BUBBLER)
	void chatShowsATextDisplayBubbleOnSpigot(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		alice.chat("hello");

		SeenEntity bubble = bob.seesAbove(alice, "hello");
		assertEquals(TEXT_DISPLAY, bubble.getType());
		bob.seesVanish(bubble);
	}

	@Test
	@Tag("compatibility")
	@Socialismus(network = Network.SPIGOT, plugins = Plugin.PACKETEVENTS, modules = Module.BUBBLER, configure = ArmorStands.class)
	void chatShowsAnArmorStandBubbleOnSpigot(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		alice.chat("hello");

		SeenEntity bubble = bob.seesAbove(alice, "hello");
		assertEquals(ARMOR_STAND, bubble.getType());
		bob.seesVanish(bubble);
	}

	@Test
	@Tag("compatibility")
	@MinecraftVersions(before = "1.19.4")
	@Socialismus(plugins = Plugin.PACKETEVENTS, modules = Module.BUBBLER)
	void textDisplaysBeforeTheirVersionAreWarnedAbout(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		// Bubbler reads its settings at the first bubble at the latest.
		alice.chat("hello");
		bob.neverSeesLabelled("hello");

		String log = Console.of(anvil).log();
		assertTrue(log.contains("Text displays need Minecraft 1.19.4 or newer"), "Bubbler did not warn:\n" + log);
	}

	/**
	 * Draws bubbles with armor stands instead of text displays. Bubbler fills in the settings left out.
	 */
	static final class ArmorStands implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.put("modules/bubbler/settings.yml", Map.of("bubbleType", "ARMOR_STAND"));
		}
	}
}
