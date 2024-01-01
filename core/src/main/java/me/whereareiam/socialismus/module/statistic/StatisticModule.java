package me.whereareiam.socialismus.module.statistic;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.module.Module;
import me.whereareiam.socialismus.util.LoggerUtil;

@Singleton
public class StatisticModule implements Module {
    private final SettingsConfig settingsConfig;
    private boolean moduleStatus;

    @Inject
    public StatisticModule(LoggerUtil loggerUtil, SettingsConfig settingsConfig) {
        this.settingsConfig = settingsConfig;

        loggerUtil.trace("Initializing class: " + this);
    }

    @Override
    public void initialize() {
        moduleStatus = true;
    }

    @Override
    public boolean isEnabled() {
        return false;
        /*return moduleStatus == settingsConfig.modules.statistic.enabled;*/
    }

    @Override
    public void reload() {

    }
}
