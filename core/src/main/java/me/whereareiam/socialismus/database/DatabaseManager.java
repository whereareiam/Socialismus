package me.whereareiam.socialismus.database;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class DatabaseManager {
    private static final Map<String, Database> databases = new HashMap<>();
    private final Injector injector;
    private final LoggerUtil loggerUtil;
    private final Plugin plugin;
    private final SettingsConfig settingsConfig;

    @Inject
    public DatabaseManager(Injector injector, LoggerUtil loggerUtil, Plugin plugin, SettingsConfig settingsConfig) {
        this.injector = injector;
        this.loggerUtil = loggerUtil;
        this.plugin = plugin;
        this.settingsConfig = settingsConfig;

        loggerUtil.trace("Initializing class: " + this);

        /*setupDatabase();*/
    }

    /*private void setupDatabase() {
        DatabaseSettingsConfig databaseConfig = settingsConfig.database;
        DatabaseType databaseType = settingsConfig.database.databaseType;
        switch (databaseType) {
            case SQLITE:
                SQLiteDatabaseConfig sqLiteDatabaseConfig = databaseConfig.sqlite;
                Path storagePath = plugin.getDataFolder().toPath().resolve(sqLiteDatabaseConfig.storageDir);

                if (!Files.exists(storagePath)) {
                    try {
                        Files.createDirectory(storagePath);
                    } catch (Exception e) {
                        loggerUtil.severe(e.getMessage());
                    }
                }

                String url = "jdbc:sqlite:" + storagePath.toFile() + "/";

                for (String file : sqLiteDatabaseConfig.files) {
                    SQLiteDatabase database = injector.getInstance(SQLiteDatabase.class);
                    database.setUrl(url + file + ".db");
                    databases.put(file, database);
                }
                break;
            case MYSQL, MARIADB:
                throw new IllegalArgumentException("MySQL/MariaDB is currently not supported");
            default:
                throw new IllegalArgumentException("Unsupported database type: " + databaseType);
        }

        for (Database database : databases.values()) {
            if (database.connect())
                loggerUtil.info("Established database connection");
        }
    }*/

    public Collection<Database> getDatabases() {
        return databases.values();
    }

    public Database getDatabase(String database) {
        return databases.get(database);
    }

    public void shutdown() {
        for (Database database : databases.values()) {
            database.disconnect();
        }
    }
}
