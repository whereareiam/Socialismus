package me.whereareiam.socialismus.common.phrase;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.common.config.provider.PhrasesProvider;
import me.whereareiam.socialismus.model.phrase.Phrases;
import me.whereareiam.socialismus.service.Phrasebook;
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
	private final Map<String, Supplier<Phrases>> modules = new LinkedHashMap<>();

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
	public @NotNull Optional<String> find(@NotNull String scope, @NotNull String key) {
		List<Supplier<Phrases>> sources = sources();
		for (String candidate : candidates(scope, key)) {
			for (Supplier<Phrases> source : sources) {
				Optional<String> text = source.get().text(candidate);
				if (text.isPresent()) return text;
			}
		}

		return Optional.empty();
	}

	@Override
	public @NotNull String render(@NotNull String scope, @NotNull String text) {
		return render(scope, text, new HashSet<>());
	}

	@Override
	public @NotNull List<String> unresolved(@NotNull String scope, @NotNull String text) {
		List<String> missing = new ArrayList<>();
		collect(scope, text, new HashSet<>(), missing);

		return List.copyOf(missing);
	}

	@Override
	public synchronized void add(@NotNull String owner, @NotNull Supplier<Phrases> phrases) {
		modules.put(owner, phrases);
	}

	@Override
	public synchronized void remove(@NotNull String owner) {
		modules.remove(owner);
	}

	/**
	 * The references of every phrase that nothing resolves, by the phrase's key.
	 *
	 * @return the keys of the references nothing resolves, by the key of the phrase that holds them
	 */
	public @NotNull Map<String, List<String>> unresolved() {
		Map<String, List<String>> problems = new LinkedHashMap<>();
		for (Supplier<Phrases> source : sources()) {
			source.get().texts().forEach((key, text) -> {
				List<String> missing = unresolved("", text);
				if (!missing.isEmpty()) problems.putIfAbsent(key, missing);
			});
		}

		return problems;
	}

	private String render(String scope, String text, Set<String> following) {
		Matcher matcher = REFERENCE.matcher(text);
		StringBuilder rendered = new StringBuilder();
		while (matcher.find()) {
			String key = matcher.group(1);
			String replacement = following.add(key)
					? find(scope, key).map(phrase -> render(scope, phrase, following)).orElse("")
					: "";
			following.remove(key);
			matcher.appendReplacement(rendered, Matcher.quoteReplacement(replacement));
		}
		matcher.appendTail(rendered);

		return rendered.toString();
	}

	private void collect(String scope, String text, Set<String> following, List<String> missing) {
		Matcher matcher = REFERENCE.matcher(text);
		while (matcher.find()) {
			String key = matcher.group(1);
			if (!following.add(key)) {
				missing.add(key);
				continue;
			}

			Optional<String> phrase = find(scope, key);
			if (phrase.isEmpty()) missing.add(key);
			else collect(scope, phrase.get(), following, missing);
			following.remove(key);
		}
	}

	private synchronized List<Supplier<Phrases>> sources() {
		List<Supplier<Phrases>> sources = new ArrayList<>();
		sources.add(core);
		sources.addAll(modules.values());

		return sources;
	}

	private static List<String> candidates(String scope, String key) {
		return scope.isEmpty() ? List.of(key) : List.of(scope + "." + key, key);
	}
}
