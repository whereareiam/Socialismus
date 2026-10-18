package me.whereareiam.socialismus.module.database.common.config.provider;

import me.whereareiam.configura.Config;
import me.whereareiam.configura.type.Format;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.module.database.common.config.DatabaseSettings;
import me.whereareiam.socialismus.module.database.common.config.SqliteSettings;
import me.whereareiam.socialismus.module.database.common.config.PostgresSettings;
import me.whereareiam.socialismus.registry.base.Registry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class DatabaseSettingsProviderTest {
	@TempDir
	private Path workingPath;

	@BeforeEach
	void configureYaml() {
		Config.setConfigured(Config.builder().format(Format.YAML).build());
	}

	@Test
	@SuppressWarnings("unchecked")
	void writesTheSqliteSettingsOnAFirstStart() throws IOException {
		DatabaseSettings settings = new DatabaseSettingsProvider(workingPath, mock(Registry.class)).get();

		assertInstanceOf(SqliteSettings.class, settings);
		String file = Files.readString(workingPath.resolve("settings.yml"));
		assertTrue(file.contains("type: \"SQLITE\""), file);
		assertTrue(file.contains("file: \"socialismus.db\""), file);
		assertFalse(file.contains("host:"), "The SQLite settings hold server settings:\n" + file);
	}

	@Test
	@SuppressWarnings("unchecked")
	void readsTheServerTheFileNames() throws IOException {
		Files.writeString(workingPath.resolve("settings.yml"), """
				type: POSTGRES
				host: db.example.org
				database: chat
				""");

		DatabaseSettings settings = new DatabaseSettingsProvider(workingPath, mock(Registry.class)).get();

		PostgresSettings postgres = assertInstanceOf(PostgresSettings.class, settings);
		assertEquals("db.example.org", postgres.getHost());
		assertEquals("chat", postgres.getDatabase());
		assertEquals(5432, postgres.getPort());
		assertEquals("postgres", postgres.getUsername());
		assertEquals(10, postgres.getPool().getMaximumSize());
	}
}
