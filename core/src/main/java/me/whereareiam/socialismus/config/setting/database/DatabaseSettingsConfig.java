package me.whereareiam.socialismus.config.setting.database;

import me.whereareiam.socialismus.database.DatabaseType;

public class DatabaseSettingsConfig {
    public DatabaseType databaseType = DatabaseType.SQLITE;
    public SQLiteDatabaseConfig sqlite = new SQLiteDatabaseConfig();
}
