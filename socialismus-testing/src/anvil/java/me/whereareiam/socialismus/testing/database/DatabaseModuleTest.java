package me.whereareiam.socialismus.testing.database;

import java.io.IOException;
import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.Network;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Console;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SocialismusDatabase on a server: it loads the driver of the database its settings choose, opens the database and
 * offers it to the other modules.
 */
@Tag("database")
class DatabaseModuleTest {
	@Test
	@Socialismus(modules = Module.DATABASE)
	void opensTheSqliteDatabaseAndRegistersItForTheOtherModules(ScenarioContext anvil) throws IOException {
		String log = Console.of(anvil).log();

		assertTrue(log.contains("Loaded module SocialismusDatabase"), "SocialismusDatabase did not load:\n" + log);
		assertTrue(log.contains("Opened the SQLITE database"), "The database was not opened:\n" + log);
		assertTrue(log.contains("Registered resource socialismusdatabase:database from module SocialismusDatabase"),
				"The database was not registered:\n" + log);

		Network network = Network.of(anvil);
		String file = network.dataPath("modules/SocialismusDatabase/socialismus.db");
		assertTrue(network.files(anvil).exists(file), "The SQLite file was not created at " + file);
	}
}
