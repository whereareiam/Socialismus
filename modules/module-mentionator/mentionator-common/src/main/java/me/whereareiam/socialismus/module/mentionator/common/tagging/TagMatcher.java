package me.whereareiam.socialismus.module.mentionator.common.tagging;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Finds the tags in a message's text, from left to right: whole words only, ignoring case, by the exact name or value
 * of a candidate. Where several tags fit at one place the one of higher priority wins, and of equals the longer.
 * Letters, digits and {@code _} end a word, so punctuation around a tag is fine, though not an {@code @} right before
 * it, as in {@code mail@Steve}; {@code \@everyone} is found as escaped.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TagMatcher {
	private static final char ESCAPE = '\\';

	/**
	 * Finds the tags in a text.
	 *
	 * @param text       the text the sender wrote
	 * @param tags       the enabled tags
	 * @param candidates the players tags may name
	 * @return the tags found, in the order they are written
	 */
	public static @NotNull List<Found> find(@NotNull String text, @NotNull List<Tag> tags, @NotNull List<Candidate> candidates) {
		List<Entry> entries = entries(tags, candidates);
		List<Found> found = new ArrayList<>();
		int position = 0;
		while (position < text.length()) {
			Found best = bestAt(text, position, entries);
			if (best == null) {
				position++;
				continue;
			}

			found.add(best);
			position = best.end();
		}

		return found;
	}

	private static @Nullable Found bestAt(String text, int position, List<Entry> entries) {
		if (position > 0 && joins(text.charAt(position - 1))) return null;

		Found best = null;
		for (Entry entry : entries) {
			Found found = entry.matchAt(text, position);
			if (found == null || !beats(found, best)) continue;

			best = found;
		}

		return best;
	}

	private static boolean beats(Found found, @Nullable Found best) {
		if (best == null) return true;

		int priority = Integer.compare(found.tag().getPriority(), best.tag().getPriority());

		return priority > 0 || priority == 0 && found.end() > best.end();
	}

	private static List<Entry> entries(List<Tag> tags, List<Candidate> candidates) {
		List<Tag> byPriority = new ArrayList<>(tags);
		byPriority.sort(Comparator.comparingInt(Tag::getPriority).reversed());

		List<Entry> entries = new ArrayList<>();
		for (Tag tag : byPriority)
			for (String written : tag.getWritten())
				entries.add(Entry.of(tag, written, candidates));

		return entries;
	}

	// What a tag cannot follow directly: a word, or an @ that makes the tag part of something else.
	private static boolean joins(char character) {
		return isWordCharacter(character) || character == '@';
	}

	static boolean isWordCharacter(char character) {
		return Character.isLetterOrDigit(character) || character == '_';
	}

	private static boolean endsWord(String text, int end) {
		return end >= text.length() || !isWordCharacter(text.charAt(end));
	}

	/**
	 * A value a form names, such as a player's name, with the candidates whose value it is.
	 */
	private record Value(String spelling, List<Candidate> candidates) {
	}

	/**
	 * One form of a tag, with the values it may name by their first letter in lower case.
	 */
	private record Entry(Tag tag, String written, Form form, Map<Character, List<Value>> values, List<Candidate> everyone) {
		static Entry of(Tag tag, String written, List<Candidate> candidates) {
			Form form = Form.of(written);
			Map<String, Value> byValue = new LinkedHashMap<>();
			for (Candidate candidate : candidates) {
				String value = valueOf(form, candidate);
				if (value == null || value.isBlank()) continue;

				byValue.computeIfAbsent(value.toLowerCase(Locale.ROOT), ignored -> new Value(value, new ArrayList<>()))
						.candidates().add(candidate);
			}

			Map<Character, List<Value>> values = new HashMap<>();
			for (Map.Entry<String, Value> value : byValue.entrySet())
				values.computeIfAbsent(value.getKey().charAt(0), ignored -> new ArrayList<>()).add(value.getValue());

			return new Entry(tag, written, form, values, candidates);
		}

		private static @Nullable String valueOf(Form form, Candidate candidate) {
			if (form.username()) return candidate.player().getUsername();
			if (form.placeholder() == null) return null;

			return candidate.values().get(form.placeholder());
		}

		@Nullable Found matchAt(String text, int position) {
			String prefix = form.prefix();
			if (!text.regionMatches(true, position, prefix, 0, prefix.length())) return null;

			int at = position + prefix.length();
			if (!form.namesValue())
				return endsWord(text, at) && at > position ? found(text, position, at, written, everyone) : null;
			if (at >= text.length()) return null;

			Value best = null;
			for (Value value : values.getOrDefault(Character.toLowerCase(text.charAt(at)), List.of())) {
				if (!fits(text, at, value.spelling())) continue;
				if (best != null && best.spelling().length() >= value.spelling().length()) continue;

				best = value;
			}
			if (best == null) return null;

			return found(text, position, at + best.spelling().length() + form.suffix().length(), best.spelling(), best.candidates());
		}

		private boolean fits(String text, int at, String value) {
			if (!text.regionMatches(true, at, value, 0, value.length())) return false;
			if (form.bare() && value.length() < minimumLength()) return false;

			int afterValue = at + value.length();
			String suffix = form.suffix();
			if (!text.regionMatches(true, afterValue, suffix, 0, suffix.length())) return false;

			return endsWord(text, afterValue + suffix.length());
		}

		private int minimumLength() {
			Integer minimum = tag.getParameters() == null ? null : tag.getParameters().getMinimumLength();

			return minimum == null ? 0 : minimum;
		}

		private Found found(String text, int start, int end, String match, List<Candidate> candidates) {
			boolean escaped = start > 0 && text.charAt(start - 1) == ESCAPE;

			return new Found(tag, form, start, end, match, candidates, escaped);
		}
	}
}
