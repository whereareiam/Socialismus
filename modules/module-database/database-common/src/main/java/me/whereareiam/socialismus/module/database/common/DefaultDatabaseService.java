package me.whereareiam.socialismus.module.database.common;

import com.zaxxer.hikari.HikariDataSource;
import me.whereareiam.dialectica.Dialectica;
import me.whereareiam.dialectica.adapter.jdbi.DialecticaPlugin;
import me.whereareiam.dialectica.adapter.jdbi.entity.EntityAccess;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.database.api.DatabaseService;
import me.whereareiam.socialismus.module.database.api.schema.SchemaInstaller;
import me.whereareiam.socialismus.module.database.common.config.DatabaseSettings;
import me.whereareiam.socialismus.module.database.common.connection.DataSourceFactory;
import me.whereareiam.socialismus.module.database.common.connection.DatabaseServerRequirement;
import me.whereareiam.socialismus.module.database.common.schema.DatabaseSchemaInstaller;
import org.jdbi.v3.core.Jdbi;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.sql.SQLException;

/**
 * The database SocialismusDatabase opens: a connection pool on the configured database, the Jdbi that speaks its
 * Dialectica dialect, and the installer of the other owners' schemas. It is open from {@link #open} until
 * {@link #close}.
 */
public final class DefaultDatabaseService implements DatabaseService, AutoCloseable {
	private final HikariDataSource dataSource;
	private final Jdbi jdbi;
	private final DatabaseSchemaInstaller schemaInstaller;

	private DefaultDatabaseService(HikariDataSource dataSource, Jdbi jdbi) {
		this.dataSource = dataSource;
		this.jdbi = jdbi;
		this.schemaInstaller = new DatabaseSchemaInstaller(jdbi);
	}

	/**
	 * Opens the database the settings choose, and refuses a server older than {@link DatabaseServerRequirement}
	 * allows. The driver of the configured database must be loaded.
	 *
	 * @param settings    the module's settings
	 * @param workingPath the module's directory
	 * @return the open database
	 * @throws SQLException when the database cannot be reached
	 */
	@NotNull
	public static DefaultDatabaseService open(@NotNull DatabaseSettings settings, @NotNull Path workingPath) throws SQLException {
		HikariDataSource dataSource = DataSourceFactory.create(settings, workingPath);
		try {
			DatabaseServerRequirement.verify(settings.getType(), dataSource);
		} catch (SQLException | RuntimeException failure) {
			dataSource.close();
			throw failure;
		}

		Jdbi jdbi = Jdbi.create(dataSource);
		jdbi.installPlugin(new DialecticaPlugin());
		Logger.info("Opened the %s database", settings.getType());

		return new DefaultDatabaseService(dataSource, jdbi);
	}

	@Override
	public <T> @NotNull EntityAccess<T> access(@NotNull Class<T> entityType) {
		return EntityAccess.of(jdbi, Dialectica.entity(entityType));
	}

	@Override
	public @NotNull SchemaInstaller getSchemaInstaller() {
		return schemaInstaller;
	}

	/**
	 * Closes the connection pool.
	 */
	@Override
	public void close() {
		dataSource.close();
		Logger.info("Closed the database");
	}
}
