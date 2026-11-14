package me.whereareiam.socialismus.module.mentionator.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.Tag;
import me.whereareiam.socialismus.module.mentionator.common.config.defaults.TagsDefaults;
import me.whereareiam.socialismus.module.mentionator.common.config.dynamic.TagsConfig;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.type.PlatformType;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * The tags of every file in {@code tags/}; an empty folder gets {@code players}, {@code groups} and {@code roles}.
 */
@Singleton
public class TagsProvider extends ConfigProvider<List<Tag>> {
	private final boolean dialogue;

	@Inject
	public TagsProvider(
			@Named("tagsPath") Path tagsPath,
			@Named("dialogue") boolean dialogue,
			Registry<Reloadable> registry
	) {
		super(tagsPath, "", listType(), registry);
		this.dialogue = dialogue;
	}

	@Override
	protected List<Tag> load() {
		return ConfigFolder.read(configura(), getPath(), TagsConfig.class, () -> TagsDefaults.files(configura(), dialogue, PlatformType.isGameServer()))
				.stream()
				.flatMap(file -> file.getTags() == null ? Stream.empty() : file.getTags().stream())
				.toList();
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends List<Tag>> listType() {
		return (Class<? extends List<Tag>>) (Class<?>) List.class;
	}
}
