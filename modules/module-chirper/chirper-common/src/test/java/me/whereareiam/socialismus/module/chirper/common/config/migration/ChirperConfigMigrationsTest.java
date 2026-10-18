package me.whereareiam.socialismus.module.chirper.common.config.migration;

import com.fasterxml.jackson.databind.JsonNode;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.Configura;
import me.whereareiam.configura.type.Format;
import me.whereareiam.strata.Strata;
import me.whereareiam.strata.adapter.configura.StrataFeature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChirperConfigMigrationsTest {
	private final Configura configura = Config.builder().format(Format.YAML).feature(new StrataFeature()).build();

	@TempDir
	Path workingPath;

	@Test
	void correctsTheSoundOfTheExampleAnnouncementAndKeepsOtherSounds() throws IOException {
		Files.writeString(workingPath.resolve("messages.yml"), "noPlayers: none\n");
		Files.createDirectories(workingPath.resolve("announcements"));
		Files.writeString(workingPath.resolve("announcements/default.yml"), """
				announcements:
				- id: titleExample1
				  contents:
				    SOUND:
				      sound: entity_player_levelup
				- id: own
				  contents:
				    SOUND:
				      sound: block.note_block.pling
				""");

		new Strata(List.of(ChirperConfigMigrations.on(configura, workingPath))).migrate();

		JsonNode announcements = configura.readNode(workingPath.resolve("announcements/default.yml")).path("announcements");
		assertEquals("entity.player.levelup", announcements.path(0).path("contents").path("SOUND").path("sound").asText());
		assertEquals("block.note_block.pling", announcements.path(1).path("contents").path("SOUND").path("sound").asText());
	}
}
