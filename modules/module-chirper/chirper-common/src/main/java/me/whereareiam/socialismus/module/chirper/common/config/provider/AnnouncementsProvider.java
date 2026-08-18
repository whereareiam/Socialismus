package me.whereareiam.socialismus.module.chirper.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.chirper.api.model.announcement.Announcement;
import me.whereareiam.socialismus.module.chirper.common.config.defaults.AnnouncementsDefaults;
import me.whereareiam.socialismus.module.chirper.common.config.dynamic.AnnouncementsConfig;
import me.whereareiam.socialismus.registry.base.Registry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Singleton
public class AnnouncementsProvider extends ConfigProvider<List<Announcement>> {
	@Inject
	@SuppressWarnings("unchecked")
	public AnnouncementsProvider(
			@Named("announcementsPath") Path announcementsPath,
			Registry<Reloadable> registry
	) {
		super(
				announcementsPath,
				"",
				listType(),
				registry
		);
	}

	@Override
	protected List<Announcement> load() {
		List<Announcement> announcements = new ArrayList<>();
		try (Stream<Path> paths = Files.list(getPath())) {
			paths.filter(Files::isRegularFile)
					.forEach(path -> {
						String fileName = path.getFileName().toString();
						int dotIndex = fileName.lastIndexOf('.');
						String baseName = dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);

						if (baseName.isEmpty()) return;

						announcements.addAll(addAnnouncementsFromConfig(path.getParent().resolve(baseName)));
					});
		} catch (IOException e) {
			Logger.severe("Failed to load announcement configurations", e);
			return Collections.emptyList();
		}

		if (announcements.isEmpty())
			announcements.addAll(addAnnouncementsFromConfig(getPath().resolve("default")));

		// Remove duplicates by ID
		announcements.removeIf(announcement -> announcements.stream()
				.anyMatch(c -> c != announcement && c.getId().equals(announcement.getId())));
		return announcements;
	}

	@Override
	protected Configura configura() {
		return versioned(super.configura().withDefaults(AnnouncementsDefaults.class), AnnouncementsConfig.class);
	}

	private List<Announcement> addAnnouncementsFromConfig(Path path) {
		AnnouncementsConfig config = configura().update(path, AnnouncementsConfig.class);
		return config.getAnnouncements().stream()
				.filter(Announcement::isEnabled)
				.toList();
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends List<Announcement>> listType() {
		return (Class<? extends List<Announcement>>) (Class<?>) List.class;
	}
}
