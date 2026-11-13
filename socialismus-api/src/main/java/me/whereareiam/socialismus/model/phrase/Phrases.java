package me.whereareiam.socialismus.model.phrase;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.whereareiam.configura.annotation.PreserveUnknownFields;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A file of phrases: texts that other config refers to by key, as {@code <ref:key>}, such as what a player is told
 * when a condition denies them. A key is the path of a text in the file, its sections joined by dots, so
 * {@code denied: { world.outside: ... }} under {@code chat:} has the key {@code chat.denied.world.outside}.
 *
 * <pre>{@code
 * denied:
 *   permission.missing: "{prefix}<white>You need <gray>{permissions}</gray> for this."
 * chat:
 *   staff-only: "<red>Only staff can write in {chat}."
 * }</pre>
 */
@Getter
@ToString
@EqualsAndHashCode
@PreserveUnknownFields
public final class Phrases {
	/**
	 * The file's entries in the order they are written: texts and sections of further entries.
	 */
	private final @NotNull SequencedMap<String, Entry> entries;

	private Phrases(@NotNull SequencedMap<String, Entry> entries) {
		this.entries = Collections.unmodifiableSequencedMap(new LinkedHashMap<>(entries));
	}

	/**
	 * Creates phrases of entries.
	 *
	 * @param entries the entries in order
	 * @return the phrases
	 */
	public static @NotNull Phrases of(@NotNull SequencedMap<String, Entry> entries) {
		return new Phrases(entries);
	}

	/**
	 * Phrases without entries.
	 *
	 * @return the phrases
	 */
	public static @NotNull Phrases none() {
		return new Phrases(new LinkedHashMap<>());
	}

	/**
	 * Starts phrases written in code, such as the defaults of a file.
	 *
	 * <pre>{@code
	 * Phrases phrases = Phrases.builder()
	 *         .section("denied", denied -> denied
	 *                 .text("world.outside", "This only works in {worlds}."))
	 *         .build();
	 * }</pre>
	 *
	 * @return a builder
	 */
	public static @NotNull Builder builder() {
		return new Builder();
	}

	/**
	 * These phrases with the entries of the defaults they lack, section by section; what is written here stays.
	 *
	 * @param defaults the phrases shipped
	 * @return the phrases with the missing defaults added after their own entries
	 */
	public @NotNull Phrases withDefaults(@NotNull Phrases defaults) {
		return new Phrases(merge("", entries, defaults.entries, texts().keySet()));
	}

	// Adds the defaults whose keys the written entries lack, however the written ones split their keys into sections.
	private static SequencedMap<String, Entry> merge(String prefix, Map<String, Entry> written, Map<String, Entry> defaults, Set<String> keys) {
		SequencedMap<String, Entry> merged = new LinkedHashMap<>(written);
		defaults.forEach((name, fallback) -> {
			Entry own = merged.get(name);
			switch (fallback) {
				case Entry.Text ignored -> {
					if (own == null && !keys.contains(prefix + name)) merged.put(name, fallback);
				}
				case Entry.Section section -> {
					Map<String, Entry> ownEntries = own instanceof Entry.Section ownSection ? ownSection.entries() : Map.of();
					if (own == null || own instanceof Entry.Section) {
						SequencedMap<String, Entry> inner = merge(prefix + name + ".", ownEntries, section.entries(), keys);
						if (!inner.isEmpty()) merged.put(name, new Entry.Section(inner));
					}
				}
			}
		});

		return merged;
	}

	/**
	 * The text of a key.
	 *
	 * @param key the key, such as {@code chat.denied.world.outside}
	 * @return the text, empty when the file has none at that key
	 */
	public @NotNull Optional<String> text(@NotNull String key) {
		return Optional.ofNullable(texts().get(key));
	}

	/**
	 * Every text of the file by its key, in the order they are written; of two entries with the same key, such as
	 * {@code world.outside} and {@code world: { outside: ... }}, the first.
	 *
	 * @return the texts
	 */
	public @NotNull SequencedMap<String, String> texts() {
		SequencedMap<String, String> texts = new LinkedHashMap<>();
		collect("", entries, texts);

		return texts;
	}

	private static void collect(String prefix, Map<String, Entry> entries, SequencedMap<String, String> texts) {
		entries.forEach((name, entry) -> {
			switch (entry) {
				case Entry.Text text -> texts.putIfAbsent(prefix + name, text.text());
				case Entry.Section section -> collect(prefix + name + ".", section.entries(), texts);
			}
		});
	}

	/**
	 * An entry of a phrases file: a text, or a section of further entries.
	 */
	public sealed interface Entry permits Entry.Text, Entry.Section {
		/**
		 * A text.
		 *
		 * @param text the text, which may refer to other phrases as {@code <ref:key>}
		 */
		record Text(@NotNull String text) implements Entry {
		}

		/**
		 * A section of further entries.
		 *
		 * @param entries the entries in the order they are written
		 */
		record Section(@NotNull SequencedMap<String, Entry> entries) implements Entry {
			/**
			 * Copies the entries, keeping their order.
			 */
			public Section {
				entries = Collections.unmodifiableSequencedMap(new LinkedHashMap<>(entries));
			}
		}
	}

	/**
	 * Builds phrases in the order their entries are added.
	 */
	public static final class Builder {
		private final SequencedMap<String, Entry> entries = new LinkedHashMap<>();

		private Builder() {
		}

		/**
		 * Adds a text.
		 *
		 * @param name the entry's name, such as {@code world.outside}
		 * @param text the text
		 * @return this builder
		 */
		public @NotNull Builder text(@NotNull String name, @NotNull String text) {
			entries.put(name, new Entry.Text(text));

			return this;
		}

		/**
		 * Adds a section of further entries.
		 *
		 * @param name    the section's name, such as {@code denied}
		 * @param section adds the section's entries
		 * @return this builder
		 */
		public @NotNull Builder section(@NotNull String name, @NotNull Consumer<Builder> section) {
			Builder builder = new Builder();
			section.accept(builder);
			entries.put(name, new Entry.Section(builder.entries));

			return this;
		}

		/**
		 * Builds the phrases.
		 *
		 * @return the phrases
		 */
		public @NotNull Phrases build() {
			return new Phrases(entries);
		}
	}
}
