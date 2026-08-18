package me.whereareiam.socialismus.module.chirper.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.chirper.api.model.announcer.Announcer;
import me.whereareiam.socialismus.module.chirper.common.config.defaults.AnnouncersDefaults;
import me.whereareiam.socialismus.module.chirper.common.config.dynamic.AnnouncersConfig;
import me.whereareiam.socialismus.registry.base.Registry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Singleton
public class AnnouncersProvider extends ConfigProvider<List<Announcer>> {
	@Inject
	@SuppressWarnings("unchecked")
	public AnnouncersProvider(
			@Named("announcersPath") Path announcersPath,
			Registry<Reloadable> registry
	) {
		super(
				announcersPath,
				"",
				listType(),
				registry
		);
	}

	@Override
	protected List<Announcer> load() {
		List<Announcer> announcers = new ArrayList<>();
		try (Stream<Path> paths = Files.list(getPath())) {
			paths.filter(Files::isRegularFile)
					.forEach(path -> {
						String fileName = path.getFileName().toString();
						int dotIndex = fileName.lastIndexOf('.');
						String baseName = dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);

						if (baseName.isEmpty()) return;

						announcers.addAll(addAnnouncersFromConfig(path.getParent().resolve(baseName)));
					});
		} catch (IOException e) {
			Logger.severe("Failed to load announcers configurations", e);
			return Collections.emptyList();
		}

		if (announcers.isEmpty())
			announcers.addAll(addAnnouncersFromConfig(getPath().resolve("default")));

		return announcers;
	}

	@Override
	protected Configura configura() {
		return versioned(super.configura().withDefaults(AnnouncersDefaults.class), AnnouncersConfig.class);
	}

	private List<Announcer> addAnnouncersFromConfig(Path path) {
		AnnouncersConfig config = configura().update(path, AnnouncersConfig.class);
		return config.getAnnouncers().stream()
				.filter(Announcer::isEnabled)
				.toList();
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends List<Announcer>> listType() {
		return (Class<? extends List<Announcer>>) (Class<?>) List.class;
	}
}
