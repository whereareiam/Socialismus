package me.whereareiam.socialismus.module.database.common.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.socialismus.module.database.common.config.DatabaseSettings;
import me.whereareiam.socialismus.module.database.common.config.DatabaseType;
import me.whereareiam.socialismus.module.database.common.config.SqliteSettings;
import me.whereareiam.socialismus.module.database.common.config.ServerSettings;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * Opens the connection pool of the configured database.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataSourceFactory {
	private static final String POOL_NAME = "SocialismusDatabase";

	/**
	 * Opens a pool on the database the settings choose. It connects right away, so a database that cannot be
	 * reached fails here, with the driver's reason.
	 *
	 * @param settings    the module's settings
	 * @param workingPath the module's directory
	 * @return the open pool
	 */
	@NotNull
	public static HikariDataSource create(@NotNull DatabaseSettings settings, @NotNull Path workingPath) {
		HikariConfig config = new HikariConfig();
		config.setPoolName(POOL_NAME);
		config.setJdbcUrl(JdbcUrlFactory.create(settings, workingPath));
		config.setDriverClassName(driverClassName(settings.getType()));

		if (settings instanceof ServerSettings server) {
			config.setUsername(server.getUsername());
			config.setPassword(server.getPassword());
		}
		// SQLite checks foreign keys, and cascades deletes along them, only on connections that turn them on.
		if (settings instanceof SqliteSettings) config.setConnectionInitSql("PRAGMA foreign_keys = ON");

		DatabaseSettings.Pool pool = settings.getPool();
		config.setMaximumPoolSize(pool.getMaximumSize());
		config.setMinimumIdle(pool.getMinimumIdle());
		config.setConnectionTimeout(pool.getConnectionTimeout().toMillis());
		config.setMaxLifetime(pool.getMaxLifetime().toMillis());

		return new HikariDataSource(config);
	}

	private static String driverClassName(DatabaseType type) {
		return switch (type) {
			case SQLITE -> "org.sqlite.JDBC";
			case MYSQL -> "org.mariadb.jdbc.Driver";
			case POSTGRES -> "org.postgresql.Driver";
		};
	}
}
