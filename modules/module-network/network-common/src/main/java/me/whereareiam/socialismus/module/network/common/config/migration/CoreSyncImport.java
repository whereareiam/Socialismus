package me.whereareiam.socialismus.module.network.common.config.migration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.network.common.config.NetworkSettings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Carries the synchronization settings Socialismus kept itself before SocialismusNetwork into SocialismusNetwork's
 * {@code settings}, on its first start: the server's name in {@code sync} becomes the node id, and what
 * {@code chat/sync} says chat does becomes {@code chat}. The two documents are then removed from Socialismus's
 * directory, which no longer reads them.
 */
public final class CoreSyncImport {
	private final Configura configura;
	private final Path dataPath;
	private final Path workingPath;

	/**
	 * @param configura   reads and writes the documents
	 * @param dataPath    Socialismus's directory, which may hold {@code sync} and {@code chat/sync}
	 * @param workingPath SocialismusNetwork's directory
	 */
	public CoreSyncImport(@NotNull Configura configura, @NotNull Path dataPath, @NotNull Path workingPath) {
		this.configura = configura;
		this.dataPath = dataPath;
		this.workingPath = workingPath;
	}

	/**
	 * Writes SocialismusNetwork's {@code settings} from the imported values when this is its first start and
	 * Socialismus's {@code sync} exists, and removes the imported documents.
	 *
	 * @param defaults the settings as SocialismusNetwork would write them on its first start
	 * @return whether anything was imported
	 */
	public boolean importInto(@NotNull NetworkSettings defaults) {
		NetworkSettings settings = defaults;
		String extension = configura.extension();
		Path sync = dataPath.resolve("sync" + extension);
		Path chatSync = dataPath.resolve("chat").resolve("sync" + extension);
		if (Files.exists(workingPath.resolve("settings" + extension)) || !Files.exists(sync)) return false;

		LegacySync legacy = configura.read(dataPath.resolve("sync"), LegacySync.class);
		if (legacy.getServer() != null && !legacy.getServer().isBlank()) settings.setNode(legacy.getServer().trim());

		if (Files.exists(chatSync)) {
			LegacyChatSync legacyChat = configura.read(dataPath.resolve("chat").resolve("sync"), LegacyChatSync.class);
			NetworkSettings.Chat chat = settings.getChat();
			chat.setEnabled(legacy.isEnabled() && legacyChat.isEnabled());
			chat.setPreserveFormat(legacyChat.isPreserveFormat());
			chat.setClearHistory(legacyChat.isClearHistory());
		}

		try {
			Files.createDirectories(workingPath);
			Files.write(workingPath.resolve("settings" + extension), configura.writeBytes(settings));
		} catch (IOException failure) {
			Logger.warn("Could not take over the synchronization settings of Socialismus: %s", failure.getMessage());
			return false;
		}

		delete(sync);
		delete(chatSync);
		Logger.info("Took over the synchronization settings of Socialismus: this node is %s", settings.getNode());

		return true;
	}

	private static void delete(Path file) {
		try {
			Files.deleteIfExists(file);
		} catch (IOException failure) {
			Logger.warn("Could not remove %s, which Socialismus no longer reads: %s", file, failure.getMessage());
		}
	}

	/**
	 * Socialismus's {@code sync}, as far as SocialismusNetwork takes it over.
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	public static class LegacySync {
		private boolean enabled;
		private String server;
	}

	/**
	 * Socialismus's {@code chat/sync}.
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	public static class LegacyChatSync {
		private boolean enabled;
		private boolean preserveFormat;
		private boolean clearHistory;
	}
}
