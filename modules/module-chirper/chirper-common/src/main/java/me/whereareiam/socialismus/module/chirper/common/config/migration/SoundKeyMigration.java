package me.whereareiam.socialismus.module.chirper.common.config.migration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import me.whereareiam.strata.MigrationAction;
import me.whereareiam.strata.adapter.configura.ConfigContext;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Corrects the sound of the {@code titleExample1} example announcement, which earlier builds wrote as
 * {@code entity_player_levelup}: no sound has that key, so players heard nothing. Only that exact key changes, in
 * every document of {@code announcements/}, so a sound an admin chose stays as it is.
 */
final class SoundKeyMigration implements MigrationAction<ConfigContext> {
	static final int VERSION = 3;
	static final String NAME = "sound-key";

	private static final String BROKEN = "entity_player_levelup";
	private static final String FIXED = "entity.player.levelup";
	private static final String FOLDER = "announcements";

	private final Path workingPath;
	private final String extension;

	SoundKeyMigration(@NotNull Path workingPath, @NotNull String extension) {
		this.workingPath = workingPath;
		this.extension = extension;
	}

	@Override
	public void apply(@NotNull ConfigContext files) throws IOException {
		for (String document : documents()) {
			for (JsonNode announcement : files.document(FOLDER + "/" + document).path("announcements")) {
				JsonNode sound = announcement.path("contents").path("SOUND");
				if (sound instanceof ObjectNode content && BROKEN.equals(content.path("sound").asText(null)))
					content.set("sound", TextNode.valueOf(FIXED));
			}
		}
	}

	private List<String> documents() throws IOException {
		Path folder = workingPath.resolve(FOLDER);
		if (!Files.isDirectory(folder)) return List.of();

		try (Stream<Path> paths = Files.list(folder)) {
			return paths
					.filter(Files::isRegularFile)
					.map(file -> file.getFileName().toString())
					.filter(name -> name.endsWith(extension))
					.sorted()
					.toList();
		}
	}
}
