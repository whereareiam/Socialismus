package me.whereareiam.socialismus.common.config.migration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
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
 * Splits the settings into one document per topic and gathers the chat documents in {@code chat/}:
 * <ul>
 *   <li>{@code settings} {@code synchronization} becomes {@code sync}, {@code updater} becomes {@code updater} with
 *   {@code check}, a duration as {@code interval} and the warnings under {@code warn}, and {@code commands} becomes
 *   {@code behavior} in {@code commands};</li>
 *   <li>{@code misc} dissolves into {@code vanilla}: {@code joinMessage} and {@code quitMessage}, which say whether
 *   the server's messages are shown instead of whether they are hidden, and {@code sending}; its
 *   {@code allowLegacyParsing}, which nothing read, is removed;</li>
 *   <li>{@code chats/settings} becomes {@code chat/settings} with its switches under {@code notifications}, the
 *   fallback's {@code chatId} as {@code chat} and its {@code synchronization} in {@code chat/sync};
 *   {@code chats/messages} becomes {@code chat/messages}, and every other file of {@code chats/}, which held chats,
 *   moves to {@code chat/list/}; one whose name starts with {@code settings} or {@code messages}, which earlier
 *   builds did not read as chats, stays where it is.</li>
 * </ul>
 * Every move does nothing where there is nothing to move, so a directory past this layout stays as it is.
 */
final class ConfigLayoutMigration implements MigrationAction<ConfigContext> {
	static final int VERSION = 3;
	static final String NAME = "config-layout";

	private final Path dataPath;
	private final String extension;

	ConfigLayoutMigration(@NotNull Path dataPath, @NotNull String extension) {
		this.dataPath = dataPath;
		this.extension = extension;
	}

	@Override
	public void apply(@NotNull ConfigContext files) throws IOException {
		String settings = "settings" + extension;
		splitSettings(files, settings);
		dissolveMisc(files, settings);
		gatherChat(files);
	}

	private void splitSettings(ConfigContext files, String settings) {
		files.moveSection(settings, "/synchronization", "sync" + extension, "");
		files.moveSection(settings, "/commands", "commands" + extension, "/behavior");

		String updater = "updater" + extension;
		files.move(settings, "/updater/checkForUpdates", updater, "/check");
		files.move(settings, "/updater/warnAboutUpdates", updater, "/warn/updates");
		files.move(settings, "/updater/warnAboutLocalBuilds", updater, "/warn/localBuilds");
		files.move(settings, "/updater/warnAboutDevBuilds", updater, "/warn/devBuilds");
		// The interval counted hours; as a duration a bare number would count minutes.
		JsonNode interval = files.remove(settings, "/updater/interval");
		if (interval != null)
			files.put(updater, "/interval", interval.isNumber() ? TextNode.valueOf(interval.asLong() + "h") : interval);
		files.removeIfEmpty(settings, "/updater");
	}

	private void dissolveMisc(ConfigContext files, String settings) {
		JsonNode hideJoin = files.remove(settings, "/misc/disableJoinNotification");
		if (hideJoin != null) files.put(settings, "/vanilla/joinMessage", BooleanNode.valueOf(!hideJoin.asBoolean()));
		JsonNode hideQuit = files.remove(settings, "/misc/disableQuitNotification");
		if (hideQuit != null) files.put(settings, "/vanilla/quitMessage", BooleanNode.valueOf(!hideQuit.asBoolean()));
		files.move(settings, "/misc/vanillaSending", settings, "/vanilla/sending");
		files.remove(settings, "/misc/allowLegacyParsing");
		files.removeIfEmpty(settings, "/misc");
	}

	private void gatherChat(ConfigContext files) throws IOException {
		String chatSettings = "chats/settings" + extension;
		for (String notification : List.of("NoChat", "NoFormat", "NoPlayers", "NoNearbyPlayers", "EmptyMessage")) {
			String key = Character.toLowerCase(notification.charAt(0)) + notification.substring(1);
			files.rename(chatSettings, "/notify" + notification, "/notifications/" + key);
		}
		files.rename(chatSettings, "/fallback/chatId", "/fallback/chat");
		files.moveSection(chatSettings, "/synchronization", "chat/sync" + extension, "");
		files.moveFile(chatSettings, "chat/settings" + extension);
		files.moveFile("chats/messages" + extension, "chat/messages" + extension);

		for (String document : documentsIn("chats"))
			files.moveFile("chats/" + document, "chat/list/" + document);
		files.deleteDirectoryIfEmpty("chats");
	}

	private List<String> documentsIn(String folder) throws IOException {
		Path path = dataPath.resolve(folder);
		if (!Files.isDirectory(path)) return List.of();

		try (Stream<Path> paths = Files.list(path)) {
			return paths
					.filter(Files::isRegularFile)
					.map(file -> file.getFileName().toString())
					.filter(name -> name.endsWith(extension))
					// Earlier builds read no file whose name starts with these as chats, so such a file stays behind.
					.filter(name -> !name.startsWith("settings") && !name.startsWith("messages"))
					.sorted()
					.toList();
		}
	}
}
