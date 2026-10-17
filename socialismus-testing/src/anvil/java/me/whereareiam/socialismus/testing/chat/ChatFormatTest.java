package me.whereareiam.socialismus.testing.chat;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.model.chat.ChatFormat;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static me.whereareiam.socialismus.testing.environment.ServerFiles.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * How a chat message looks: the format a chat picks for its sender, and how the format is serialized.
 */
@Tag("chat")
class ChatFormatTest {
	@Test
	@Socialismus(configure = StaffFormat.class)
	void messageTakesTheLastFormatWhoseRequirementsTheSenderMeets(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();
		Player bob = Player.joins(anvil, "Bob");

		alice.chat("from the staff");
		bob.sees("[Staff] Alice: from the staff");

		bob.chat("from a player");
		alice.sees("[G] Bob: from a player");
	}

	@Test
	@Socialismus(configure = LegacyFormat.class)
	void legacySerializationRendersTheFormatLikeMiniMessage(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		alice.chat("in colour");

		String message = bob.sees("[G] Alice: in colour");
		assertFalse(message.contains("&"), "The legacy codes were not rendered: " + message);
	}

	/**
	 * Global chat formats the messages of its staff, who hold the permission, differently.
	 */
	static final class StaffFormat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(chat("global", 0, "", 0, "[G] {playerName}: {message}").toBuilder()
					.formats(List.of(
							new ChatFormat("[G] {playerName}: {message}", Map.of()),
							new ChatFormat("[Staff] {playerName}: {message}",
									requirements(permission("socialismus.chat.staff"), null))
					))
					.build());
		}
	}

	/**
	 * Socialismus reads formats with legacy ampersand codes.
	 */
	static final class LegacyFormat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(chat("global", 0, "", 0, "&7[G] {playerName}: &f{message}"));
			files.put("settings.yml", Map.of("serialization", Map.of("type", "LEGACY_AMPERSAND")));
		}
	}
}
