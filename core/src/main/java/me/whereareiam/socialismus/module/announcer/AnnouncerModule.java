package me.whereareiam.socialismus.module.announcer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.module.Module;
import me.whereareiam.socialismus.util.LoggerUtil;

import java.io.File;
import java.nio.file.Path;

@Singleton
public class AnnouncerModule implements Module {
    private final LoggerUtil loggerUtil;
    private final SettingsConfig settingsConfig;
    private final Path announcerPath;
    private boolean moduleStatus = false;

    @Inject
    public AnnouncerModule(LoggerUtil loggerUtil, SettingsConfig settingsConfig, @Named("modulePath") Path modulePath) {
        this.settingsConfig = settingsConfig;
        this.loggerUtil = loggerUtil;

        this.announcerPath = modulePath.resolve("announcer");

        loggerUtil.trace("Initializing class: " + this);
    }

    private void registerAnnouncers() {
    }

    @Override
    public void initialize() {
        File announcerDir = announcerPath.toFile();
        if (!announcerDir.exists()) {
            boolean isCreated = announcerDir.mkdir();
            loggerUtil.debug("Creating announcer dir");
            if (!isCreated) {
                loggerUtil.severe("Failed to create directory: " + announcerPath);
            }
        }

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
