package me.whereareiam.socialismus.module.announcer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.module.Module;
import me.whereareiam.socialismus.util.LoggerUtil;

@Singleton
public class AnnouncerModule implements Module {
    private final SettingsConfig settingsConfig;
    private boolean moduleStatus = false;

    @Inject
    public AnnouncerModule(LoggerUtil loggerUtil, SettingsConfig settingsConfig) {
        this.settingsConfig = settingsConfig;

        loggerUtil.trace("Initializing class: " + this);
    }

    private void registerAnnouncers() {
    }

    @Override
    public void initialize() {
        registerAnnouncers();

        moduleStatus = true;
    }

    @Override
    public boolean isEnabled() {
        return moduleStatus == settingsConfig.modules.announcer;
    }

    @Override
    public void reload() {

    }
}
