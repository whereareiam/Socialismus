package me.whereareiam.socialismus.common.phrase;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.common.config.provider.PhrasesProvider;
import me.whereareiam.socialismus.model.phrase.Phrases;
import me.whereareiam.socialismus.service.phrase.PhraseScope;
import me.whereareiam.socialismus.service.phrase.PhraseSource;
import me.whereareiam.socialismus.service.phrase.Phrasebook;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Looks phrases up in {@code phrases.yml} and then in the modules' files, in the scope first.
 */
@Singleton
public class DefaultPhrasebook implements Phrasebook {
	private static final Pattern REFERENCE = Pattern.compile("<ref:([^<>\\s]+)>");

	private final Supplier<Phrases> core;
	private final Map<String, Source> modules = new LinkedHashMap<>();

	@Inject
	public DefaultPhrasebook(PhrasesProvider core) {
		this(core::get);
	}

	/**
	 * Creates the service over Socialismus's own phrases.
	 *
	 * @param core the phrases of {@code phrases.yml} as they are now
	 */
	public DefaultPhrasebook(Supplier<Phrases> core) {
		this.core = core;
	}

	@Override
	public @NotNull PhraseScope scope(@NotNull String scope) {
		return new Scope(scope);
	}

	@Override
	public synchronized @NotNull PhraseSource add(@NotNull String owner, @NotNull Supplier<Phrases> phrases) {
		Source source = new Source(owner, phrases);
		modules.put(owner, source);

		return source;
	}

	/**
	 * The references of every phrase that nothing resolves, by the phrase's key.
	 *
	 * @return the keys of the references nothing resolves, by the key of the phrase that holds them
	 */
	public @NotNull Map<String, List<String>> unresolved() {
		Scope none = new Scope("");
		Map<String, List<String>> problems = new LinkedHashMap<>();
		for (Supplier<Phrases> source : sources()) {
			source.get().texts().forEach((key, text) -> {
				List<String> missing = none.unresolved(text);
				if (!missing.isEmpty()) problems.putIfAbsent(key, missing);
			});
		}

		return problems;
	}

	private synchronized List<Supplier<Phrases>> sources() {
		List<Supplier<Phrases>> sources = new ArrayList<>();
		sources.add(core);
		modules.values().forEach(source -> sources.add(source.phrases));

		return sources;
	}

	private synchronized void remove(Source source) {
		modules.remove(source.owner, source);
	}

	private final class Scope implements PhraseScope {
		private final String name;
		private final List<String> prefixes;

		private Scope(String name) {
			this.name = name;
			this.prefixes = name.isEmpty() ? List.of("") : List.of(name + ".", "");
		}

		@Override
		public @NotNull String name() {
			return name;
		}

		@Override
		public @NotNull Optional<String> find(@NotNull String key) {
			List<Supplier<Phrases>> sources = sources();
			for (String prefix : prefixes) {
				for (Supplier<Phrases> source : sources) {
					Optional<String> text = source.get().text(prefix + key);
					if (text.isPresent()) return text;
				}
			}

			return Optional.empty();
		}

		@Override
		public @NotNull String render(@NotNull String text) {
			return render(text, new HashSet<>());
		}

		@Override
		public @NotNull List<String> unresolved(@NotNull String text) {
			List<String> missing = new ArrayList<>();
			collect(text, new HashSet<>(), missing);

			return List.copyOf(missing);
		}

		private String render(String text, Set<String> following) {
			Matcher matcher = REFERENCE.matcher(text);
			StringBuilder rendered = new StringBuilder();
			while (matcher.find()) {
				String key = matcher.group(1);
				String replacement = following.add(key)
						? find(key).map(phrase -> render(phrase, following)).orElse("")
						: "";
				following.remove(key);
				matcher.appendReplacement(rendered, Matcher.quoteReplacement(replacement));
			}
			matcher.appendTail(rendered);

			return rendered.toString();
		}

		private void collect(String text, Set<String> following, List<String> missing) {
			Matcher matcher = REFERENCE.matcher(text);
			while (matcher.find()) {
				String key = matcher.group(1);
				if (!following.add(key)) {
					missing.add(key);
					continue;
				}

				Optional<String> phrase = find(key);
				if (phrase.isEmpty()) missing.add(key);
				else collect(phrase.get(), following, missing);
				following.remove(key);
			}
		}
	}

	private final class Source implements PhraseSource {
		private final String owner;
		private final Supplier<Phrases> phrases;

		private Source(String owner, Supplier<Phrases> phrases) {
			this.owner = owner;
			this.phrases = phrases;
		}

		@Override
		public void close() {
			remove(this);
		}
	}
}
