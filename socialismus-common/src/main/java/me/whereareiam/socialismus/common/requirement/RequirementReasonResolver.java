package me.whereareiam.socialismus.common.requirement;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.model.config.message.Messages;
import me.whereareiam.socialismus.model.requirement.RequirementReason;
import me.whereareiam.socialismus.service.requirement.RequirementReasonService;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Words a failed requirement from the feature's texts, then from those of {@code messages.yml}.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class RequirementReasonResolver implements RequirementReasonService {
	private final Provider<Messages> messages;

	@Override
	public @NotNull Optional<RequirementReason> resolve(
			@NotNull Verdict.Failed failed,
			@NotNull Map<String, String> wording,
			@NotNull Map<String, String> placeholders
	) {
		Optional<String> key = failed.key();
		if (key.isEmpty()) return Optional.empty();

		Optional<String> text = textOf(wording, key.get()).or(() -> textOf(messages.get().getRequirements(), key.get()));
		if (text.isEmpty()) return Optional.empty();

		Map<String, String> filled = new HashMap<>(placeholders);
		filled.putAll(failed.reason().map(Reason::values).orElse(Map.of()));

		return Optional.of(RequirementReason.of(text.get(), filled));
	}

	private static Optional<String> textOf(Map<String, String> texts, String key) {
		if (texts == null) return Optional.empty();

		return Optional.ofNullable(texts.get(key)).filter(text -> !text.isBlank());
	}
}
