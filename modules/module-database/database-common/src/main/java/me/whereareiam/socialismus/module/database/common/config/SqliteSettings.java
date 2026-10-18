package me.whereareiam.socialismus.module.database.common.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A SQLite database: a file in the module's directory, with nothing to set up, for one server only.
 */
@Getter
@Setter
@ToString(callSuper = true)
public final class SqliteSettings extends DatabaseSettings {
	/**
	 * The file, relative to the module's directory.
	 */
	private String file = "socialismus.db";

	public SqliteSettings() {
		super(DatabaseType.SQLITE);
	}
}
