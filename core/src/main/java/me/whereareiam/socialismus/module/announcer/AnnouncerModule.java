package me.whereareiam.socialismus.module.announcer;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.config.module.announcer.AnnouncerConfig;
import me.whereareiam.socialismus.config.module.announcer.AnnouncerSettingsConfig;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.model.announcement.Announcement;
import me.whereareiam.socialismus.module.Module;
import me.whereareiam.socialismus.util.LoggerUtil;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
public class AnnouncerModule implements Module {
    private final Injector injector;
    private final LoggerUtil loggerUtil;
    private final SettingsConfig settingsConfig;
    private final AnnouncerSettingsConfig announcerSettingsConfig;
    private final Path announcerPath;
    private final List<Announcement> announcements = new ArrayList<>();

    private boolean moduleStatus = false;

    @Inject
    public AnnouncerModule(Injector injector, LoggerUtil loggerUtil, SettingsConfig settingsConfig,
                           AnnouncerSettingsConfig announcerSettingsConfig, @Named("modulePath") Path modulePath) {
        this.injector = injector;
        this.loggerUtil = loggerUtil;
        this.settingsConfig = settingsConfig;
        this.announcerSettingsConfig = announcerSettingsConfig;

        this.announcerPath = modulePath.resolve("announcer");

        loggerUtil.trace("Initializing class: " + this);
    }

    private void registerAnnouncers() {
        loggerUtil.debug("Reloading announcer.yml");
        announcerSettingsConfig.reload(announcerPath.resolve("announcer.yml"));

        loggerUtil.debug("Registering announcements");
        List<File> files = Arrays.stream(announcerPath.toFile().listFiles())
                .filter(file -> file.getName().endsWith(".yml"))
                .filter(file -> !file.getName().equals("announcer.yml"))
                .toList();

        if (files.isEmpty()) {
            loggerUtil.debug("Creating an example announcement, because dir is empty");
            AnnouncerConfig announcerConfig = createExampleAnnouncerConfig();
            announcerConfig.reload(announcerPath.resolve("example.yml"));
            announcements.addAll(announcerConfig.announcements);
        } else {
            for (File file : files) {
                loggerUtil.trace("Trying to register announcements in config: " + file.getName());
                AnnouncerConfig announcerConfig = injector.getInstance(AnnouncerConfig.class);
                announcerConfig.reload(file.toPath());
                List<Announcement> enabledAnnouncements = new ArrayList<>(announcerConfig.announcements.stream()
                        .filter(announcement -> announcement.enabled)
                        .toList());
                if (!enabledAnnouncements.isEmpty()) {
                    loggerUtil.trace("Adding new announcements (" + enabledAnnouncements.size() + ")");
                    enabledAnnouncements.addAll(announcements);
                }
            }
        }
    }

    private AnnouncerConfig createExampleAnnouncerConfig() {
        AnnouncerConfig announcerConfig = injector.getInstance(AnnouncerConfig.class);

        // TODO create example announcement

        return announcerConfig;
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
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
