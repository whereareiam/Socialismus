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

	@TempDir
	Path dataPath;

	@Test
	void removesTheVersionsEarlierBuildsWroteAndKeepsItInTheSettings() throws IOException {
		write("settings.yml", "_version: 1\nlevel: 2\n");
		write("messages.yml", "_version: 1\nprefix: \"<gray>\"\n");
		write("commands.yml", "_version: 1\nmain: socialismus\n");
		write("chats/settings.yml", "_version: 1\nselection: first\n");
		write("chats/messages.yml", "_version: 1\nnoChat: none\n");
		write("chats/chats-default.yml", "_version: 1\nchats: []\n");

		migrate();

		assertEquals(2, read("settings.yml").path("_version").asInt());
		assertEquals(2, read("settings.yml").path("level").asInt());
		for (String document : List.of("messages.yml", "commands.yml", "chats/settings.yml", "chats/messages.yml", "chats/chats-default.yml"))
			assertFalse(read(document).has("_version"), document + " still has _version");
		assertEquals("socialismus", read("commands.yml").path("main").asText());
		assertTrue(read("chats/chats-default.yml").path("chats").isArray());
	}

	@Test
	void runsEveryMigrationOnSettingsWithoutAVersion() throws IOException {
		write("settings.yml", "level: 2\n");
		write("messages.yml", "_version: 1\nprefix: \"<gray>\"\n");

		MigrationReport report = migrate();

		assertFalse(report.isEmpty());
		assertEquals(2, read("settings.yml").path("_version").asInt());
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
