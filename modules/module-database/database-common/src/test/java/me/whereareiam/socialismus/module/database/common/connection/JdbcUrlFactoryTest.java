package me.whereareiam.socialismus.module.database.common.connection;

import me.whereareiam.socialismus.module.database.common.config.SqliteSettings;
import me.whereareiam.socialismus.module.database.common.config.MysqlSettings;
import me.whereareiam.socialismus.module.database.common.config.PostgresSettings;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JdbcUrlFactoryTest {
	private static final Path WORKING_PATH = Path.of("/server/plugins/Socialismus/modules/SocialismusDatabase");

	@Test
	void resolvesTheSqliteFileInTheModuleDirectory() {
		assertEquals("jdbc:sqlite:" + WORKING_PATH.resolve("socialismus.db").toAbsolutePath(),
				JdbcUrlFactory.create(new SqliteSettings(), WORKING_PATH));
	}

	@Test
	void usesTheDefaultFileForABlankOne() {
		SqliteSettings settings = new SqliteSettings();
		settings.setFile(" ");

		assertEquals("jdbc:sqlite:" + WORKING_PATH.resolve("socialismus.db").toAbsolutePath(),
				JdbcUrlFactory.create(settings, WORKING_PATH));
	}

	@Test
	void reachesMySqlThroughTheMariaDbDriverWithItsParameters() {
		MysqlSettings settings = new MysqlSettings();
		settings.setParameters("useSSL=true");

		assertEquals("jdbc:mariadb://localhost:3306/socialismus?useSSL=true", JdbcUrlFactory.create(settings, WORKING_PATH));
	}

	@Test
	void reachesPostgresWithoutParameters() {
		assertEquals("jdbc:postgresql://localhost:5432/socialismus", JdbcUrlFactory.create(new PostgresSettings(), WORKING_PATH));
	}
}
