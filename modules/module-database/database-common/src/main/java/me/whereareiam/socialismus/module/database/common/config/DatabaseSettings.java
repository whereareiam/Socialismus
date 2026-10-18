package me.whereareiam.socialismus.module.database.common.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.configura.feature.polymorphic.api.annotation.Polymorphic;

import java.time.Duration;

/**
 * The database SocialismusDatabase opens, {@code settings} in its directory. Its {@link #type} decides which
 * settings the file holds: a SQLite file, or a MySQL or PostgreSQL server. Changes take effect on the next start.
 *
 * <pre>{@code
 * type: POSTGRES
 * host: localhost
 * port: 5432
 * database: socialismus
 * username: postgres
 * password: ""
 * parameters: ""
 * pool:
 *   maximumSize: 10
 *   ...
 * }</pre>
 */
@Getter
@Setter
@ToString
@Polymorphic(
		discriminator = "type",
		mappings = {
				@Polymorphic.Type(value = "SQLITE", target = SqliteSettings.class),
				@Polymorphic.Type(value = "MYSQL", target = MysqlSettings.class),
				@Polymorphic.Type(value = "POSTGRES", target = PostgresSettings.class)
		},
		defaultValue = "SQLITE"
)
public abstract sealed class DatabaseSettings permits SqliteSettings, ServerSettings {
	/**
	 * Which database to open; it decides the other settings of the file.
	 */
	private DatabaseType type;

	/**
	 * The connections kept open to the database.
	 */
	private Pool pool = new Pool();

	protected DatabaseSettings(DatabaseType type) {
		this.type = type;
	}

	/**
	 * The connection pool.
	 */
	@Getter
	@Setter
	@ToString
	public static class Pool {
		/** The most connections open at once. */
		private int maximumSize = 10;
		/** The connections kept open while idle. */
		private int minimumIdle = 2;
		/** How long to wait for a free connection before giving up. */
		private Duration connectionTimeout = Duration.ofSeconds(10);
		/** How long one connection is kept before it is replaced. */
		private Duration maxLifetime = Duration.ofMinutes(30);
	}
}
