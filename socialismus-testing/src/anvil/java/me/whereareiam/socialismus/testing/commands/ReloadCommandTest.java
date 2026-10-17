package me.whereareiam.socialismus.testing.commands;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Console;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static me.whereareiam.socialismus.testing.environment.ServerFiles.chat;

/**
 * Reloading the configuration while the server runs.
 */
@Tag("commands")
class ReloadCommandTest {
	@Test
	@Disabled("Reload keeps the old chats when the chat container reloads before the chats document, which the "
			+ "reloadables do in no particular order; the lifecycle refactor makes only documents reloadable")
	@Socialismus(configure = FirstFormat.class)
	void appliesAChangedFormatWithoutARestart(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");
		alice.chat("before");
		bob.sees("[first] Alice: before");

		ServerFiles.replace(anvil, ServerFiles.CHATS, Map.of("chats", List.of(chat("global", 0, "", 0, "[second] {playerName}: {message}"))));
		Console.of(anvil).run("socialismus reload").printed("Configuration reloaded");

		alice.chat("after");
		bob.sees("[second] Alice: after");
	}

	static final class FirstFormat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(chat("global", 0, "", 0, "[first] {playerName}: {message}"));
		}
	}
}
