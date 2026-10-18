package me.whereareiam.socialismus.module.database.common.config;

import lombok.ToString;

/**
 * A PostgreSQL server.
 */
@ToString(callSuper = true)
public final class PostgresSettings extends ServerSettings {
	public PostgresSettings() {
		super(DatabaseType.POSTGRES, 5432, "postgres");
	}
}
