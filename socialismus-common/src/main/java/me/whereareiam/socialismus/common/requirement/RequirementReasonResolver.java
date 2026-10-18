package me.whereareiam.socialismus.common.requirement;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.config.message.Messages;
import me.whereareiam.socialismus.model.config.message.RequirementReasons;
import me.whereareiam.socialismus.model.requirement.RequirementDenial;
import me.whereareiam.socialismus.model.requirement.RequirementReason;
import me.whereareiam.socialismus.model.requirement.RequirementResult;
import me.whereareiam.socialismus.service.requirement.RequirementReasonService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class RequirementReasonResolver implements RequirementReasonService {
	private final Provider<Messages> messages;

	@Override
	public @Nullable RequirementReason resolve(
			@NotNull RequirementResult result,
			@Nullable Map<String, RequirementReasons> wording,
			@NotNull Map<String, String> placeholders
	) {
		if (result.isPassed()) return null;

		RequirementDenial denial = result.getDenial();
		String message = result.getReason();
		if (message == null && denial != null) message = word(denial, wording);
		if (message == null) return null;

		Map<String, String> filled = new HashMap<>(placeholders);
		if (denial != null) filled.putAll(denial.getPlaceholders());

		return RequirementReason.of(message, filled);
	}

	private @Nullable String word(RequirementDenial denial, @Nullable Map<String, RequirementReasons> wording) {
		String featureWording = wordingOf(wording, denial);
		if (featureWording != null) return featureWording;

		String coreWording = wordingOf(messages.get().getRequirements(), denial);
		if (coreWording != null) return coreWording;

		String message = denial.getMessage();
		return message == null || message.isBlank() ? null : message;
	}

	private static @Nullable String wordingOf(@Nullable Map<String, RequirementReasons> wording, RequirementDenial denial) {
		if (wording == null) return null;

		RequirementReasons reasons = wording.get(denial.getKey());
		return reasons == null ? null : reasons.of(denial.getFailure());
	}
}
