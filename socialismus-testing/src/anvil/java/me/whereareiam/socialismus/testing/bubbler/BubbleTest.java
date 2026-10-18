package me.whereareiam.socialismus.testing.bubbler;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.anvil.capability.entities.model.SeenEntity;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.Plugin;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Bubbler's bubble above a player who chats or runs {@code /socialismus bubble}, as a nearby player sees it, with
 * each render strategy.
 */
@Tag("bubbler")
class BubbleTest {
	private static final String TEXT_DISPLAY = "minecraft:text_display";
	private static final String ARMOR_STAND = "minecraft:armor_stand";

	@Test
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
	@Socialismus(plugins = Plugin.PACKETEVENTS, modules = Module.BUBBLER, configure = ArmorStands.class)
	void bubbleCommandShowsAnArmorStandBubble(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();
		Player bob = Player.joins(anvil, "Bob");

		alice.command("socialismus bubble hello");

		SeenEntity bubble = bob.seesAbove(alice, "hello");
		assertEquals(ARMOR_STAND, bubble.getType());
		bob.seesVanish(bubble);
	}

	/**
	 * Draws bubbles with armor stands instead of text displays. Bubbler fills in the settings left out.
	 */
	static final class ArmorStands implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.put("modules/Bubbler/settings.yml", Map.of("bubbleType", "ARMOR_STAND"));
		}
	}
}
