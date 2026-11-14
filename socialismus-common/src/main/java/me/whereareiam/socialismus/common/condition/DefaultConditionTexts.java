package me.whereareiam.socialismus.common.condition;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.conditia.model.Part;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.model.phrase.Phrase;
import me.whereareiam.socialismus.service.condition.ConditionTexts;
import me.whereareiam.socialismus.service.phrase.PhraseScope;
import me.whereareiam.socialismus.service.phrase.Phrasebook;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Words a failed condition from the {@code denied} text of its line, or else from the {@code denied.*} phrase of
 * its reason, and adds the line's {@code hint}.
 */
@Singleton
public class DefaultConditionTexts implements ConditionTexts {
	private final PhraseScope none;

	@Inject
	public DefaultConditionTexts(@NotNull Phrasebook phrasebook) {
		this.none = phrasebook.scope("");
	}

	@Override
	public @NotNull Optional<Phrase> denied(
			@NotNull Verdict.Failed failed,
			@NotNull PhraseScope scope,
			@NotNull Map<String, String> placeholders
	) {
		Optional<String> denied = failed.wording(Word.DENIED.word()).or(() -> phrase(failed, scope));
		Optional<String> hint = failed.wording(Word.HINT.word());

		String message = Stream.of(denied, hint)
				.flatMap(Optional::stream)
				.map(scope::render)
				.filter(text -> !text.isBlank())
				.collect(Collectors.joining("\n"));
		if (message.isEmpty()) return Optional.empty();

		Map<String, String> filled = new HashMap<>(placeholders);
		filled.putAll(failed.reason().map(Reason::values).orElse(Map.of()));

		return Optional.of(Phrase.of(message, filled));
	}

	// The phrase of the reason: in the feature's scope before Socialismus's own, and in each for the part the
	// condition was about before anyone. A blank phrase leaves the reason to the next.
	private Optional<String> phrase(Verdict.Failed failed, PhraseScope scope) {
		Optional<String> key = failed.key();
		if (key.isEmpty()) return Optional.empty();

		List<String> keys = new ArrayList<>();
		for (Part<?> part : failed.parts())
			keys.add(Word.DENIED.word() + "." + part.getName() + "." + key.get());
		keys.add(Word.DENIED.word() + "." + key.get());

		List<String> candidates = new ArrayList<>();
		if (!scope.name().isEmpty()) keys.forEach(each -> candidates.add(scope.name() + "." + each));
		candidates.addAll(keys);

		return candidates.stream()
				.map(none::find)
				.flatMap(Optional::stream)
				.filter(text -> !text.isBlank())
				.findFirst();
	}
}
