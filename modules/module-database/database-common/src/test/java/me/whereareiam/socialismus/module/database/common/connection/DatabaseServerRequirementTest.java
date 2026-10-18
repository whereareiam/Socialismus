package me.whereareiam.socialismus.module.database.common.connection;

import me.whereareiam.socialismus.module.database.common.config.DatabaseType;
import me.whereareiam.socialismus.module.database.common.exception.UnsupportedDatabaseServerException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DatabaseServerRequirementTest {
	@Test
	void acceptsMySqlFrom8019() {
		assertDoesNotThrow(() -> DatabaseServerRequirement.verify(DatabaseType.MYSQL, "MySQL", "8.0.19"));
		assertDoesNotThrow(() -> DatabaseServerRequirement.verify(DatabaseType.MYSQL, "MySQL", "8.4.2-log"));
	}

	@Test
	void refusesAnOlderMySql() {
		assertThrows(UnsupportedDatabaseServerException.class,
				() -> DatabaseServerRequirement.verify(DatabaseType.MYSQL, "MySQL", "5.7.44-log"));
		assertThrows(UnsupportedDatabaseServerException.class,
				() -> DatabaseServerRequirement.verify(DatabaseType.MYSQL, "MySQL", "8.0.18"));
	}

	@Test
	void refusesAMySqlVersionItCannotRead() {
		assertThrows(UnsupportedDatabaseServerException.class,
				() -> DatabaseServerRequirement.verify(DatabaseType.MYSQL, "MySQL", "unknown"));
	}

	@Test
	void setsNoMinimumForMariaDb() {
		assertDoesNotThrow(() -> DatabaseServerRequirement.verify(DatabaseType.MYSQL, "MariaDB", "5.5.68"));
	}
}
