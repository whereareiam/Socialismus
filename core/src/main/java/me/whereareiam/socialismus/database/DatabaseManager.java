package me.whereareiam.socialismus.database;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.config.setting.database.SQLiteDatabaseConfig;
import me.whereareiam.socialismus.database.sql.SQLiteDatabase;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;

import java.nio.file.Files;
import java.nio.file.Path;

@Singleton
public class DatabaseManager {
    private final Injector injector;
    private final LoggerUtil loggerUtil;
    private final Plugin plugin;
    private final SettingsConfig settingsConfig;
    private Database database;

    @Inject
    public DatabaseManager(Injector injector, LoggerUtil loggerUtil, Plugin plugin, SettingsConfig settingsConfig) {
        this.injector = injector;
        this.loggerUtil = loggerUtil;
        this.plugin = plugin;
        this.settingsConfig = settingsConfig;

        setupDatabase();
    }

    private void setupDatabase() {
        DatabaseType databaseType = settingsConfig.database.databaseType;
        switch (databaseType) {
            case SQLITE:
                database = injector.getInstance(SQLiteDatabase.class);
                initializeSQLite();
                break;
            default:
                throw new IllegalArgumentException("Unsupported database type: " + databaseType);
        }
    }

    private void initializeSQLite() {
        SQLiteDatabaseConfig databaseConfig = settingsConfig.database.sqlite;
        Path storagePath = plugin.getDataFolder().toPath().resolve(databaseConfig.storageDir);
        if (!Files.exists(storagePath)) {
            try {
                Files.createDirectory(storagePath);
            } catch (Exception e) {
                loggerUtil.severe(e.getMessage());
            }
        }
    }
}
