package me.whereareiam.socialismus.module.database.common.config;

import lombok.ToString;

/**
 * A MySQL 8.0.19 or newer, or a MariaDB, server.
 */
@ToString(callSuper = true)
public final class MysqlSettings extends ServerSettings {
	public MysqlSettings() {
		super(DatabaseType.MYSQL, 3306, "root");
	}
}
