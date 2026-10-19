package me.whereareiam.socialismus.module.network.common.config.migration;

import me.whereareiam.configura.Config;
import me.whereareiam.configura.Configura;
import me.whereareiam.configura.type.Format;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.module.network.common.config.NetworkSettings;
import me.whereareiam.socialismus.module.network.common.config.defaults.NetworkSettingsDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class CoreSyncImportTest {
	private final Configura configura = Config.builder().format(Format.YAML).build();

	@TempDir
	private Path dataPath;

	@BeforeAll
	static void initLogger() {
		Logger.init(mock(LoggingHelper.class));
	}

	@Test
	void takesOverTheServerNameAndWhatChatsDidAndRemovesTheOldDocuments() throws IOException {
		Path workingPath = dataPath.resolve("modules/SocialismusNetwork");
		Files.writeString(dataPath.resolve("sync.yml"), "enabled: true\nserver: \"lobby-1\"\nuseRealServerName: false\n");
		Files.createDirectories(dataPath.resolve("chat"));
		Files.writeString(dataPath.resolve("chat/sync.yml"), "enabled: true\npreserveFormat: false\nclearHistory: true\n");

		boolean imported = new CoreSyncImport(configura, dataPath, workingPath).importInto(defaults());

		assertTrue(imported);
		NetworkSettings written = configura.read(workingPath.resolve("settings"), NetworkSettings.class);
		assertEquals("lobby-1", written.getNode());
		assertTrue(written.getChat().isEnabled());
		assertFalse(written.getChat().isPreserveFormat());
		assertTrue(written.getChat().isClearHistory());
		assertFalse(Files.exists(dataPath.resolve("sync.yml")));
		assertFalse(Files.exists(dataPath.resolve("chat/sync.yml")));
	}

	@Test
	void chatsStayOnTheirServerWhenSocialismusDidNotSync() throws IOException {
		Path workingPath = dataPath.resolve("modules/SocialismusNetwork");
		Files.writeString(dataPath.resolve("sync.yml"), "enabled: false\nserver: \"lobby-1\"\n");
		Files.createDirectories(dataPath.resolve("chat"));
		Files.writeString(dataPath.resolve("chat/sync.yml"), "enabled: true\npreserveFormat: true\nclearHistory: true\n");

		new CoreSyncImport(configura, dataPath, workingPath).importInto(defaults());

		assertFalse(configura.read(workingPath.resolve("settings"), NetworkSettings.class).getChat().isEnabled());
	}

	@Test
	void importsNothingOnceSocialismusNetworkHasItsSettings() throws IOException {
		Path workingPath = Files.createDirectories(dataPath.resolve("modules/SocialismusNetwork"));
		Files.writeString(workingPath.resolve("settings.yml"), "node: \"own\"\n");
		Files.writeString(dataPath.resolve("sync.yml"), "enabled: true\nserver: \"lobby-1\"\n");

		assertFalse(new CoreSyncImport(configura, dataPath, workingPath).importInto(defaults()));
		assertTrue(Files.exists(dataPath.resolve("sync.yml")));
	}

	@Test
	void importsNothingWithoutTheOldDocuments() {
		assertFalse(new CoreSyncImport(configura, dataPath, dataPath.resolve("modules/SocialismusNetwork")).importInto(defaults()));
	}

	private static NetworkSettings defaults() {
		return new NetworkSettingsDefaults().supply(new NetworkSettings());
	}
}
