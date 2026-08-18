package me.whereareiam.socialismus.module.bubbler.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.common.CommonConfiguration;
import me.whereareiam.socialismus.module.bubbler.common.config.defaults.BubblesDefaults;
import me.whereareiam.socialismus.module.bubbler.common.config.dynamic.BubblesConfig;
import me.whereareiam.socialismus.registry.base.Registry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Singleton
public class BubblesProvider extends ConfigProvider<List<Bubble>> {
	@Inject
	public BubblesProvider(
			@Named("bubblesPath") Path bubblesPath,
			Registry<Reloadable> registry
	) {
		super(
				bubblesPath,
				"",
				listType(),
				registry
		);
	}

	@Override
	protected List<Bubble> load() {
		List<Bubble> bubbles = new ArrayList<>();
		try (Stream<Path> paths = Files.list(getPath())) {
			paths.filter(Files::isRegularFile)
					.forEach(path -> {
						String fileName = path.getFileName().toString();
						int dotIndex = fileName.lastIndexOf('.');
						String baseName = dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);

						if (baseName.isEmpty()) return;

						bubbles.addAll(addBubblesFromConfig(path.getParent().resolve(baseName)));
					});
		} catch (IOException e) {
			Logger.severe("Failed to load bubble configurations", e);
			return Collections.emptyList();
		}

		if (bubbles.isEmpty())
			bubbles.addAll(addBubblesFromConfig(getPath().resolve("default")));

		// Remove duplicates by ID
		bubbles.removeIf(bubble -> bubbles.stream()
				.anyMatch(c -> c != bubble && c.getId().equals(bubble.getId())));
		return bubbles;
	}

	@Override
	protected Configura configura() {
		return versioned(
				super.configura()
						.withDefaults(BubblesDefaults.class)
						.withFeature(CommonConfiguration.createRequirementPolymorphicFeature()),
				BubblesConfig.class
		);
	}

	private List<Bubble> addBubblesFromConfig(Path path) {
		BubblesConfig config = configura().update(path, BubblesConfig.class);
		return config.getBubbles().stream()
				.filter(Bubble::isEnabled)
				.toList();
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends List<Bubble>> listType() {
		return (Class<? extends List<Bubble>>) (Class<?>) List.class;
	}
}
