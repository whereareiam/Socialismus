package me.whereareiam.socialismus.module.database.common.config;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * The databases SocialismusDatabase connects to.
 */
public enum DatabaseType {
	/** A file in the module's directory; nothing to set up, but one server only. */
	SQLITE,
	/** A MySQL 8.0.19 or newer, or a MariaDB, server; several servers can share it. */
	MYSQL,
	/** A PostgreSQL server; several servers can share it. */
	POSTGRES;

	/**
	 * Returns the Attache group of SocialismusDatabase that holds this database's driver.
	 */
	@NotNull
	public String driverGroup() {
		return name().toLowerCase(Locale.ROOT);
	}
}
