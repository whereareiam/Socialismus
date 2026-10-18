package me.whereareiam.socialismus.module.database.common;

import me.whereareiam.socialismus.module.database.common.config.DatabaseSettings;
import me.whereareiam.socialismus.module.database.common.config.SqliteSettings;

/**
 * Runs the database contract on SQLite, in a file of the module's directory, without a server.
 */
class SqliteDatabaseServiceTest extends DatabaseServiceContract {
	@Override
	protected DatabaseSettings settings() {
		return new SqliteSettings();
	}
}
