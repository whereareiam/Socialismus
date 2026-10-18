package me.whereareiam.socialismus.module.database.common.connection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.socialismus.module.database.common.config.DatabaseSettings;
import me.whereareiam.socialismus.module.database.common.config.SqliteSettings;
import me.whereareiam.socialismus.module.database.common.config.MysqlSettings;
import me.whereareiam.socialismus.module.database.common.config.PostgresSettings;
import me.whereareiam.socialismus.module.database.common.config.ServerSettings;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * Builds the JDBC URL of the configured database.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JdbcUrlFactory {
	private static final String DEFAULT_SQLITE_FILE = "socialismus.db";

	/**
	 * Returns the URL of the database the settings choose.
	 *
	 * @param settings    the module's settings
	 * @param workingPath the module's directory, which a relative SQLite file is resolved against
	 * @return the JDBC URL, without credentials
	 */
	@NotNull
	public static String create(@NotNull DatabaseSettings settings, @NotNull Path workingPath) {
		return switch (settings) {
			case SqliteSettings sqlite -> "jdbc:sqlite:" + workingPath.resolve(sqliteFile(sqlite)).toAbsolutePath();
			case MysqlSettings mysql -> server("jdbc:mariadb://", mysql);
			case PostgresSettings postgres -> server("jdbc:postgresql://", postgres);
		};
	}

	private static String sqliteFile(SqliteSettings sqlite) {
		String file = sqlite.getFile();

		return file == null || file.isBlank() ? DEFAULT_SQLITE_FILE : file;
	}

	private static String server(String prefix, ServerSettings server) {
		String url = prefix + server.getHost() + ":" + server.getPort() + "/" + server.getDatabase();
		String parameters = server.getParameters();

		return parameters == null || parameters.isBlank() ? url : url + "?" + parameters;
	}
}
