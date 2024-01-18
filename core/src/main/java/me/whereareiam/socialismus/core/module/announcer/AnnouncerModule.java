package me.whereareiam.socialismus.core.module.announcer;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.api.model.announcement.Announcement;
import me.whereareiam.socialismus.api.model.announcer.Announcer;
import me.whereareiam.socialismus.api.type.AnnouncementSelectionType;
import me.whereareiam.socialismus.core.config.module.announcer.AnnouncementConfig;
import me.whereareiam.socialismus.core.config.module.announcer.AnnouncerConfig;
import me.whereareiam.socialismus.core.config.setting.SettingsConfig;
import me.whereareiam.socialismus.core.module.Module;
import me.whereareiam.socialismus.core.util.LoggerUtil;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

@Singleton
public class AnnouncerModule implements Module {
	private final Injector injector;
	private final LoggerUtil loggerUtil;
	private final SettingsConfig settingsConfig;
	private final AnnouncerConfig announcerConfig;
	private final Path announcerPath;

	private final List<Announcement> announcements = new ArrayList<>();
	private final Map<Announcer, List<Announcement>> announcers = new HashMap<>();

	private boolean moduleStatus = false;

	@Inject
	public AnnouncerModule(Injector injector, LoggerUtil loggerUtil, SettingsConfig settingsConfig,
						   AnnouncerConfig announcerConfig, @Named("modulePath") Path modulePath) {
		this.injector = injector;
		this.loggerUtil = loggerUtil;
		this.settingsConfig = settingsConfig;
		this.announcerConfig = announcerConfig;

		this.announcerPath = modulePath.resolve("announcer");

		loggerUtil.trace("Initializing class: " + this);
	}

	private void registerAnnouncements() {
		loggerUtil.debug("Registering announcements");
		List<File> files = Arrays.stream(announcerPath.toFile().listFiles())
				.filter(file -> file.getName().endsWith(".yml"))
				.filter(file -> ! file.getName().equals("announcer.yml"))
				.toList();

		if (files.isEmpty()) {
			loggerUtil.debug("Creating an example announcement, because dir is empty");
			AnnouncementConfig announcementConfig = createExampleAnnouncementConfig();
			announcementConfig.reload(announcerPath.resolve("example.yml"));
			announcements.addAll(announcementConfig.announcements);
		} else {
			for (File file : files) {
				loggerUtil.trace("Trying to register announcements in config: " + file.getName());
				AnnouncementConfig announcementConfig = injector.getInstance(AnnouncementConfig.class);
				announcementConfig.reload(file.toPath());
				List<Announcement> enabledAnnouncements = new ArrayList<>(announcementConfig.announcements.stream()
						.filter(announcement -> announcement.enabled)
						.toList());
				if (! enabledAnnouncements.isEmpty()) {
					loggerUtil.trace("Adding new announcements (" + enabledAnnouncements.size() + ")");
					announcements.addAll(enabledAnnouncements);
				}
			}
		}
	}

	private void registerAnnouncers() {
		loggerUtil.debug("Registering announcers");
		announcerConfig.reload(announcerPath.resolve("announcer.yml"));

		if (announcerConfig.announcers.isEmpty()) {
			loggerUtil.debug("Creating an example announcer, because config is empty");
			createExampleAnnouncerConfig();
			announcerConfig.save(announcerPath.resolve("announcer.yml"));
		}

		for (Announcer announcer : announcerConfig.announcers) {
			if (announcer.enabled) {
				List<Announcement> validAnnouncements = new ArrayList<>();
				for (String announcementId : announcer.announcements) {
					Optional<Announcement> announcement = announcements.stream()
							.filter(a -> a.id.equals(announcementId))
							.findFirst();
					announcement.ifPresent(validAnnouncements::add);
				}
				announcers.put(announcer, validAnnouncements);
			}
		}
	}

	private void createExampleAnnouncerConfig() {
		Announcer announcer = new Announcer();

		announcer.enabled = true;
		announcer.interval = 10;
		announcer.selectionType = AnnouncementSelectionType.SEQUENTIAL;
		announcer.announcements = List.of("example");

		announcerConfig.announcers.add(announcer);
	}

	private AnnouncementConfig createExampleAnnouncementConfig() {
		AnnouncementConfig announcementConfig = injector.getInstance(AnnouncementConfig.class);
		Announcement announcement = injector.getInstance(Announcement.class);

		announcement.id = "example";
		announcement.enabled = true;

		announcement.message.add("<red>This is an example announcement");
		announcement.message.add("<red>You can edit this in announcer/example.yml");

		announcement.settings.delay = 10;
		announcement.settings.repeat = true;

		announcement.requirements.enabled = true;
		announcement.requirements.permission = "";
		announcement.requirements.worlds = Arrays.asList("world", "world_nether", "world_the_end");

		announcementConfig.announcements.add(announcement);

		return announcementConfig;
	}

	public List<Announcement> getAnnouncements() {
		return announcements;
	}

	public Map<Announcer, List<Announcement>> getAnnouncers() {
		return announcers;
	}

	@Override
	public void initialize() {
		File announcerDir = announcerPath.toFile();
		if (! announcerDir.exists()) {
			boolean isCreated = announcerDir.mkdir();
			loggerUtil.debug("Creating announcer dir");
			if (! isCreated) {
				loggerUtil.severe("Failed to create directory: " + announcerPath);
			}
		}

		registerAnnouncements();
		registerAnnouncers();

		moduleStatus = true;
		injector.getInstance(AnnouncerService.class).startAnnouncers();
	}

	@Override
	public boolean isEnabled() {
		return moduleStatus == settingsConfig.modules.announcer;
	}

	@Override
	public void reload() {
		injector.getInstance(AnnouncerService.class).stopAnnouncers();
		announcements.clear();
		announcers.clear();

		registerAnnouncements();
		registerAnnouncers();
		injector.getInstance(AnnouncerService.class).startAnnouncers();
	}
}
