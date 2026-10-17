package me.whereareiam.socialismus.common.config.migration;

import com.fasterxml.jackson.databind.JsonNode;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.Configura;
import me.whereareiam.configura.type.Format;
import me.whereareiam.strata.Strata;
import me.whereareiam.strata.adapter.configura.StrataFeature;
import me.whereareiam.strata.model.MigrationReport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SocialismusConfigMigrationsTest {
	private final Configura configura = Config.builder()
			.format(Format.YAML)
			.feature(new StrataFeature())
			.build();

	/** The version of the core's last migration. */
	private static final int LATEST = 3;

	@TempDir
	Path dataPath;

	@Test
	void splitsTheSettingsAndGathersTheChatDocuments() throws IOException {
		write("settings.yml", """
				level: 2
				synchronization:
				  enabled: true
				  server: "lobby"
				updater:
				  checkForUpdates: true
				  warnAboutUpdates: false
				  warnAboutLocalBuilds: true
				  warnAboutDevBuilds: false
				  interval: 6
				commands:
				  useBrigadier: true
				misc:
				  disableJoinNotification: true
				  disableQuitNotification: false
				  allowLegacyParsing: true
				  vanillaSending: false
				""");
		write("commands.yml", "commands: {}\n");
		write("chats/settings.yml", """
				notifyNoChat: false
				notifyEmptyMessage: true
				fallback:
				  enabled: true
				  chatId: "global"
				synchronization:
				  enabled: true
				  clearHistory: true
				""");
		write("chats/messages.yml", "noChatMatch: \"none\"\n");
		write("chats/chats-default.yml", "chats: []\n");
		write("chats/staff.yml", "chats: []\n");
		write("chats/settings-backup.yml", "notifyNoChat: true\n");

		migrate();

		JsonNode settings = read("settings.yml");
		assertEquals(2, settings.path("level").asInt());
		assertFalse(settings.has("synchronization") || settings.has("updater") || settings.has("commands") || settings.has("misc"));
		assertFalse(settings.path("vanilla").path("joinMessage").asBoolean());
		assertTrue(settings.path("vanilla").path("quitMessage").asBoolean());
		assertFalse(settings.path("vanilla").path("sending").asBoolean());

		assertEquals("lobby", read("sync.yml").path("server").asText());
		assertTrue(read("sync.yml").path("enabled").asBoolean());

		JsonNode updater = read("updater.yml");
		assertTrue(updater.path("check").asBoolean());
		assertEquals("6h", updater.path("interval").asText());
		assertFalse(updater.path("warn").path("updates").asBoolean());
		assertTrue(updater.path("warn").path("localBuilds").asBoolean());

		assertTrue(read("commands.yml").path("behavior").path("useBrigadier").asBoolean());
		assertTrue(read("commands.yml").has("commands"));

		JsonNode chatSettings = read("chat/settings.yml");
		assertFalse(chatSettings.path("notifications").path("noChat").asBoolean());
		assertTrue(chatSettings.path("notifications").path("emptyMessage").asBoolean());
		assertEquals("global", chatSettings.path("fallback").path("chat").asText());
		assertFalse(chatSettings.has("synchronization"));
		assertTrue(read("chat/sync.yml").path("clearHistory").asBoolean());
		assertEquals("none", read("chat/messages.yml").path("noChatMatch").asText());
		assertTrue(Files.exists(dataPath.resolve("chat/list/chats-default.yml")));
		assertTrue(Files.exists(dataPath.resolve("chat/list/staff.yml")));

		// Earlier builds did not read it as chats, so it does not become chats now.
		assertTrue(Files.exists(dataPath.resolve("chats/settings-backup.yml")));
		assertFalse(Files.exists(dataPath.resolve("chat/list/settings-backup.yml")));
	}

	@Test
	void removesTheVersionsEarlierBuildsWroteAndKeepsItInTheSettings() throws IOException {
		write("settings.yml", "_version: 1\nlevel: 2\n");
		write("messages.yml", "_version: 1\nprefix: \"<gray>\"\n");
		write("commands.yml", "_version: 1\nmain: socialismus\n");
		write("chats/settings.yml", "_version: 1\nselection: first\n");
		write("chats/messages.yml", "_version: 1\nnoChat: none\n");
		write("chats/chats-default.yml", "_version: 1\nchats: []\n");

		migrate();

		assertEquals(LATEST, read("settings.yml").path("_version").asInt());
		assertEquals(2, read("settings.yml").path("level").asInt());
		for (String document : List.of("messages.yml", "commands.yml", "chat/settings.yml", "chat/messages.yml", "chat/list/chats-default.yml"))
			assertFalse(read(document).has("_version"), document + " still has _version");
		assertEquals("socialismus", read("commands.yml").path("main").asText());
		assertTrue(read("chat/list/chats-default.yml").path("chats").isArray());
	}

	@Test
	void runsEveryMigrationOnSettingsWithoutAVersion() throws IOException {
		write("settings.yml", "level: 2\n");
		write("messages.yml", "_version: 1\nprefix: \"<gray>\"\n");

		MigrationReport report = migrate();

		assertFalse(report.isEmpty());
		assertEquals(LATEST, read("settings.yml").path("_version").asInt());
		assertFalse(read("messages.yml").has("_version"));
	}

	@Test
	void runsNothingOnANewInstallation() {
		MigrationReport report = migrate();

		assertTrue(report.isEmpty());
		assertTrue(strata().pending().values().stream().allMatch(List::isEmpty));
	}

	@Test
	void runsNothingTheSecondTime() throws IOException {
		write("settings.yml", "_version: 1\nlevel: 2\n");
		migrate();

		assertTrue(migrate().isEmpty());
	}

	private MigrationReport migrate() {
		return strata().migrate();
	}

	private Strata strata() {
		return new Strata(List.of(SocialismusConfigMigrations.on(configura, dataPath)));
	}

	private void write(String file, String content) throws IOException {
		Path path = dataPath.resolve(file);
		Files.createDirectories(path.getParent());
		Files.writeString(path, content);
	}

	private JsonNode read(String file) {
		return configura.readNode(dataPath.resolve(file));
	}
}
