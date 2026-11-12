package me.whereareiam.socialismus.common.requirement;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.model.Part;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.model.requirement.RequirementReason;
import me.whereareiam.socialismus.model.requirement.RequirementWording;
import me.whereareiam.socialismus.service.phrase.PhraseService;
import me.whereareiam.socialismus.service.requirement.RequirementReasonService;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Words a failed requirement from the {@code denied} text of its line, or else from the {@code denied.*} phrase of
 * its reason, and adds the line's {@code hint}.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class RequirementReasonResolver implements RequirementReasonService {
	private final PhraseService phrases;

	@Override
	public @NotNull Optional<RequirementReason> resolve(
			@NotNull Verdict.Failed failed,
			@NotNull String scope,
			@NotNull Map<String, String> placeholders
	) {
		Optional<String> denied = failed.wording(RequirementWording.DENIED).or(() -> phrase(failed, scope));
		Optional<String> hint = failed.wording(RequirementWording.HINT);

		String message = Stream.of(denied, hint)
				.flatMap(Optional::stream)
				.map(text -> phrases.render(scope, text))
				.filter(text -> !text.isBlank())
				.collect(Collectors.joining("\n"));
		if (message.isEmpty()) return Optional.empty();

		Map<String, String> filled = new HashMap<>(placeholders);
		filled.putAll(failed.reason().map(Reason::values).orElse(Map.of()));

		return Optional.of(RequirementReason.of(message, filled));
	}

	// The phrase of the reason: in the feature's scope before Socialismus's own, and in each for the part the
	// condition was about before anyone. A blank phrase leaves the reason to the next.
	private Optional<String> phrase(Verdict.Failed failed, String scope) {
		Optional<String> key = failed.key();
		if (key.isEmpty()) return Optional.empty();

		List<String> keys = new ArrayList<>();
		for (Part<?> part : failed.parts())
			keys.add(RequirementWording.DENIED + "." + part.getName() + "." + key.get());
		keys.add(RequirementWording.DENIED + "." + key.get());

		List<String> candidates = new ArrayList<>();
		if (!scope.isEmpty()) keys.forEach(each -> candidates.add(scope + "." + each));
		candidates.addAll(keys);

		return candidates.stream()
				.map(each -> phrases.find("", each))
				.flatMap(Optional::stream)
				.filter(text -> !text.isBlank())
				.findFirst();
	}
}
