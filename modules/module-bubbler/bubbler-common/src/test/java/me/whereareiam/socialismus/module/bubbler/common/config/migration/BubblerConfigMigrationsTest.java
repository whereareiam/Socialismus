package me.whereareiam.socialismus.module.bubbler.common.config.migration;

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

import static org.junit.jupiter.api.Assertions.*;

class BubblerConfigMigrationsTest {
	private final Configura configura = Config.builder().format(Format.YAML).feature(new StrataFeature()).build();

	@TempDir
	Path workingPath;

	@Test
	void renamesTheNotifySwitches() throws IOException {
		Files.writeString(workingPath.resolve("settings.yml"), """
				minRecipients: 1
				notify:
				  notifyNoPlayers: false
				  notifyNoNearbyPlayers: true
				  notifyNoBubbleSelected: true
				""");

		new Strata(List.of(BubblerConfigMigrations.on(configura, workingPath))).migrate();

		JsonNode settings = configura.readNode(workingPath.resolve("settings.yml"));
		assertFalse(settings.has("notify"));
		assertFalse(settings.path("notifications").path("noPlayers").asBoolean());
		assertTrue(settings.path("notifications").path("noNearbyPlayers").asBoolean());
		assertTrue(settings.path("notifications").path("noBubbleSelected").asBoolean());
		assertEquals(1, settings.path("minRecipients").asInt());
	}
}
