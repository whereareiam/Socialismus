package me.whereareiam.socialismus.testing.environment;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.Configura;
import me.whereareiam.configura.type.Format;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatFormat;
import me.whereareiam.socialismus.model.chat.trigger.SymbolChatTrigger;
import me.whereareiam.socialismus.type.chat.TriggerType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The files a journey gives Socialismus before it starts, by their path in its data directory, written through
 * Socialismus's own models. Documents a journey leaves out are created by Socialismus with
 * its defaults.
 *
 * <pre>{@code
 * static final class GlobalChat implements Consumer<ServerFiles> {
 *     public void accept(ServerFiles files) {
 *         files.chats(ServerFiles.chat("global", 0, "", 0, "[G] {playerName}: {message}"));
 *     }
 * }
 * }</pre>
 */
public final class ServerFiles {
	/** The file a journey's chats are written to; Socialismus reads every file in {@code chat/list/}. */
	public static final String CHATS = "chat/list/default.yml";

	// The documents journeys write hold no Adventure types, so plain YAML writes them as Socialismus does.
	private static final Configura CONFIGURA = Config.builder()
			.format(Format.YAML)
			.build();

	private final Map<String, Object> documents = new LinkedHashMap<>();

	/**
	 * Replaces the chats with the given ones.
	 *
	 * @param chats the chats, in any order; their priority decides
	 */
	public void chats(@NotNull Chat... chats) {
		put(CHATS, Map.of("chats", List.of(chats)));
	}

	/**
	 * Adds or replaces a document.
	 *
	 * @param path     path in the data directory, extension included
	 * @param document the model or map to write
	 */
	public void put(@NotNull String path, @NotNull Object document) {
		documents.put(path, document);
	}

	/**
	 * Returns the documents written as text, by their path.
	 */
	@NotNull
	Map<String, String> files() {
		Map<String, String> files = new LinkedHashMap<>();
		documents.forEach((path, document) -> files.put(path, write(document)));

		return files;
	}

	/**
	 * Describes a chat that a symbol at the start of a message selects, with one format every participant sees.
	 *
	 * @param id       the chat's identifier
	 * @param priority which chat wins when several match; the highest
	 * @param symbol   the symbol that selects the chat, empty for every message
	 * @param radius   how far, in blocks, a message reaches; 0 for everyone
	 * @param format   the format, with {@code {playerName}} and {@code {message}}
	 */
	@NotNull
	public static Chat chat(@NotNull String id, int priority, @NotNull String symbol, int radius, @NotNull String format) {
		return Chat.builder()
				.id(id)
				.priority(priority)
				.enabled(true)
				.triggers(List.of(SymbolChatTrigger.builder()
						.type(TriggerType.SYMBOL)
						.strip(true)
						.radius(radius)
						.symbol(symbol)
						.build()))
				.formats(List.of(new ChatFormat(format, Map.of())))
				.requirements(Map.of())
				.build();
	}

	/**
	 * Replaces a document in the data directory of a running network, for a journey that changes it and then
	 * reloads Socialismus.
	 *
	 * @param anvil    the running network
	 * @param path     path in the data directory, extension included
	 * @param document the model or map to write
	 */
	public static void replace(@NotNull ScenarioContext anvil, @NotNull String path, @NotNull Object document) {
		Path file = Network.of(anvil).dataDirectory(anvil).resolve(path);
		try {
			Files.writeString(file, write(document), StandardCharsets.UTF_8);
		} catch (IOException failure) {
			throw new UncheckedIOException("Could not write " + file, failure);
		}
	}

	/**
	 * Reads a document from the data directory of a running network as text.
	 *
	 * @param anvil the running network
	 * @param path  path in the data directory, extension included
	 */
	@NotNull
	public static String read(@NotNull ScenarioContext anvil, @NotNull String path) {
		Path file = Network.of(anvil).dataDirectory(anvil).resolve(path);
		try {
			return Files.readString(file, StandardCharsets.UTF_8);
		} catch (IOException failure) {
			throw new UncheckedIOException("Could not read " + file, failure);
		}
	}

	private static String write(Object document) {
		return new String(CONFIGURA.writeBytes(document), StandardCharsets.UTF_8);
	}
}
