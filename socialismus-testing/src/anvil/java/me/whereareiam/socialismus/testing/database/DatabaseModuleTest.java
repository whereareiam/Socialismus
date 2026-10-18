package me.whereareiam.socialismus.testing.database;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.Network;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Console;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SocialismusDatabase on a server: it loads the driver of the database its settings choose, opens the database and
 * offers it to the other modules.
 */
@Tag("database")
class DatabaseModuleTest {
	@Test
	@Socialismus(modules = Module.DATABASE)
	void opensTheSqliteDatabaseAndRegistersItForTheOtherModules(ScenarioContext anvil) {
		String log = Console.of(anvil).log();

		assertTrue(log.contains("Loaded module SocialismusDatabase"), "SocialismusDatabase did not load:\n" + log);
		assertTrue(log.contains("Opened the SQLITE database"), "The database was not opened:\n" + log);
		assertTrue(log.contains("Registered resource socialismusdatabase:database from module SocialismusDatabase"),
				"The database was not registered:\n" + log);

		Path file = Network.of(anvil).dataDirectory(anvil).resolve("modules/SocialismusDatabase/socialismus.db");
		assertTrue(Files.isRegularFile(file), "The SQLite file was not created at " + file);
	}
}
