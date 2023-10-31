package me.whereareiam.socialismus.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.setting.SettingsConfig;

@Singleton
public class DatabaseManager {
    private final SettingsConfig settingsConfig;

    @Inject
    public DatabaseManager(SettingsConfig settingsConfig) {
        this.settingsConfig = settingsConfig;
    }
}
