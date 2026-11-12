package me.whereareiam.socialismus.testing.environment;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.conditia.adapter.configura.ConditiaFeature;
import me.whereareiam.conditia.model.Node;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.Configura;
import me.whereareiam.configura.type.Format;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatFormat;
import me.whereareiam.socialismus.model.chat.trigger.RegexChatTrigger;
import me.whereareiam.socialismus.model.chat.trigger.SymbolChatTrigger;
import me.whereareiam.socialismus.model.requirement.RequirementLists;
import me.whereareiam.socialismus.type.chat.TriggerType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
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
	/** The chat settings: the fallback chat and which notices a sender gets. */
	public static final String CHAT_SETTINGS = "chat/settings.yml";
	/** The messages of chat, such as the notice that no chat matched. */
	public static final String CHAT_MESSAGES = "chat/messages.yml";
	/** The messages of Socialismus itself, such as the reasons its requirements tell a player. */
	public static final String MESSAGES = "messages.yml";

	// The documents journeys write hold no Adventure types, so plain YAML writes them as Socialismus does, with the
	// requires: lists of Conditia.
	private static final Configura CONFIGURA = Config.builder()
			.format(Format.YAML)
			.feature(new ConditiaFeature())
			.build();

	private final Map<String, Object> documents = new LinkedHashMap<>();
	private final Map<String, String> texts = new LinkedHashMap<>();

	/**
	 * Replaces the chats with the given ones.
	 *
	 * @param chats the chats, in any order; their priority decides
	 */
	public void chats(@NotNull Chat... chats) {
		put(CHATS, Map.of("chats", List.of(chats)));
	}

	/**
	 * Replaces the chats of one file in {@code chat/list/}, for a journey with chats in several files.
	 *
	 * @param file  the file's name, extension included
	 * @param chats the chats of the file
	 */
	public void chatsIn(@NotNull String file, @NotNull Chat... chats) {
		put("chat/list/" + file, Map.of("chats", List.of(chats)));
	}

	/**
	 * Adds or replaces a document.
	 *
	 * @param path     path in the data directory, extension included
	 * @param document the model or map to write
	 */
	public void put(@NotNull String path, @NotNull Object document) {
		texts.remove(path);
		documents.put(path, document);
	}

	/**
	 * Adds or replaces a document as written text, for values a model cannot hold before Anvil fills them in, such
	 * as the port of a container in {@code ${anvil.process.redis.port}}.
	 *
	 * @param path path in the data directory, extension included
	 * @param text the document's text
	 */
	public void text(@NotNull String path, @NotNull String text) {
		documents.remove(path);
		texts.put(path, text);
	}

	/**
	 * Returns the documents written as text, by their path.
	 */
	@NotNull
	Map<String, String> files() {
		Map<String, String> files = new LinkedHashMap<>();
		documents.forEach((path, document) -> files.put(path, write(document)));
		files.putAll(texts);

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
				.formats(List.of(new ChatFormat(format, Requirements.none())))
				.build();
	}

	/**
	 * Describes a chat that a message matching a regular expression selects, kept as it was written.
	 *
	 * @param id       the chat's identifier
	 * @param priority which chat wins when several match; the highest
	 * @param pattern  the expression the whole message matches
	 * @param format   the format, with {@code {playerName}} and {@code {message}}
	 */
	@NotNull
	public static Chat regexChat(@NotNull String id, int priority, @NotNull String pattern, @NotNull String format) {
		return chat(id, priority, "", 0, format).toBuilder()
				.triggers(List.of(RegexChatTrigger.builder()
						.type(TriggerType.REGEX)
						.strip(false)
						.radius(0)
						.pattern(pattern)
						.build()))
				.build();
	}

	/**
	 * Returns a list a player meets when they hold every given permission.
	 */
	@NotNull
	public static Requirements permission(@NotNull String... permissions) {
		return RequirementLists.permission(permissions);
	}

	/**
	 * Returns a list a player meets when they stand in one of the given worlds.
	 */
	@NotNull
	public static Requirements world(@NotNull String... worlds) {
		return RequirementLists.of("world", worlds);
	}

	/**
	 * Returns a list a player meets when they are on one of the given servers.
	 */
	@NotNull
	public static Requirements server(@NotNull String... servers) {
		return RequirementLists.of("server", servers);
	}

	/**
	 * Returns a list the message being sent meets when it contains the given text.
	 */
	@NotNull
	public static Requirements content(@NotNull String text) {
		return RequirementLists.of("content", text);
	}

	/**
	 * Returns a list met when the given list is not: {@code - not: [...]}.
	 */
	@NotNull
	public static Requirements not(@NotNull Requirements requires) {
		return Requirements.of(new Node.Sequence(List.of(Node.Mapping.of("not", requires.getConditions()))));
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
		Network network = Network.of(anvil);
		try {
			network.files(anvil).write(network.dataPath(path), write(document));
		} catch (IOException failure) {
			throw new UncheckedIOException("Could not write " + network.dataPath(path), failure);
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
		Network network = Network.of(anvil);
		try {
			return network.files(anvil).read(network.dataPath(path));
		} catch (IOException failure) {
			throw new UncheckedIOException("Could not read " + network.dataPath(path), failure);
		}
	}

	private static String write(Object document) {
		return new String(CONFIGURA.writeBytes(document), StandardCharsets.UTF_8);
	}
}
