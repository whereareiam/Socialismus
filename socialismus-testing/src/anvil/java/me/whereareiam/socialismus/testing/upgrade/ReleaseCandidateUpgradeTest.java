package me.whereareiam.socialismus.testing.upgrade;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Network;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Starting on the data directory 2.0.0-RC6 left. The fixture in {@code data/2.0.0-RC6} is what RC6 wrote on its
 * first start, with two edits an admin could have made: the fallback chat's format and the message prefix.
 */
@Tag("upgrade")
class ReleaseCandidateUpgradeTest {
	private static final Pattern VERSION = Pattern.compile("(?m)^_version: (\\d+)$");

	@Test
	@Socialismus(data = "data/2.0.0-RC6")
	void startsOnTheDirectoryOfRc6AndKeepsItsEdits(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		alice.chat("after the upgrade");
		bob.sees("[Before upgrade] Alice: after the upgrade");

		Matcher version = VERSION.matcher(ServerFiles.read(anvil, "settings.yml"));
		assertTrue(version.find() && Integer.parseInt(version.group(1)) > 0, "settings.yml does not record the version of its migrations");
		for (String document : List.of("messages.yml", "commands.yml", "sync.yml", "updater.yml", "chat/settings.yml", "chat/messages.yml", "chat/sync.yml", "chat/list/chats-default.yml"))
			assertFalse(VERSION.matcher(ServerFiles.read(anvil, document)).find(), document + " carries a _version");
		assertTrue(ServerFiles.read(anvil, "messages.yml").contains("<gold>Kept <dark_gray>| "), "The edited message prefix was lost");

		// RC6 kept its chats in chats/ and its sync, updater and command behaviour in settings.yml.
		assertFalse(Files.exists(Network.of(anvil).dataDirectory(anvil).resolve("chats")), "chats/ was left behind");
		assertTrue(ServerFiles.read(anvil, "chat/list/chats-default.yml").contains("[Before upgrade]"), "The edited chat did not move to chat/list");
		String settings = ServerFiles.read(anvil, "settings.yml");
		assertFalse(settings.contains("synchronization:") || settings.contains("updater:") || settings.contains("misc:"), "settings.yml still holds what moved out: " + settings);
		assertTrue(ServerFiles.read(anvil, "updater.yml").contains("interval: \"1h\""), "The updater interval did not become a duration");
		String chatSettings = ServerFiles.read(anvil, "chat/settings.yml");
		assertFalse(chatSettings.contains("permission: \"socialismus.admin\"") || chatSettings.contains("bypassPermission:"), "Chat history kept its permissions: " + chatSettings);
		assertTrue(chatSettings.contains("bypassRequirements:"), "Chat history's permissions did not become requirement groups: " + chatSettings);
	}
}
