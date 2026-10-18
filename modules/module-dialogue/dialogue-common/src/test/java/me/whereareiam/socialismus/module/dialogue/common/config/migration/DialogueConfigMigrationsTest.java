package me.whereareiam.socialismus.module.dialogue.common.config.migration;

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
import static org.junit.jupiter.api.Assertions.assertFalse;

class DialogueConfigMigrationsTest {
	private final Configura configura = Config.builder().format(Format.YAML).feature(new StrataFeature()).build();

	@TempDir
	Path workingPath;

	@Test
	void leavesANewInstallationToItsDefaults() {
		new Strata(List.of(DialogueConfigMigrations.on(configura, workingPath))).migrate();

		assertFalse(Files.exists(workingPath.resolve("settings.yml")));
	}

	@Test
	void keepsTheSettingsOfAnInstallation() throws IOException {
		Path settings = workingPath.resolve("settings.yml");
		Files.writeString(settings, "messaging:\n  reply:\n    toOffline: true\n");

		new Strata(List.of(DialogueConfigMigrations.on(configura, workingPath))).migrate();

		assertEquals(true, configura.readNode(settings).path("messaging").path("reply").path("toOffline").asBoolean());
	}
}
