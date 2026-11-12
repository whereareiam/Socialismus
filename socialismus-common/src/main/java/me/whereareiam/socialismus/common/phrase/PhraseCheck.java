package me.whereareiam.socialismus.common.phrase;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.requirement.RequirementWording;

import java.util.List;
import java.util.Map;

/**
 * Tells the admin, once the modules have registered their condition types, which reasons no phrase words and which
 * phrases refer to phrases no file has. Players denied for such a reason get the feature's generic notice, and a
 * reference nothing resolves is left out.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class PhraseCheck {
	private final Conditions conditions;
	private final DefaultPhraseService phrases;

	/**
	 * Logs what is missing.
	 */
	public void run() {
		for (Map.Entry<String, ConditionType<?>> type : conditions.types().entrySet()) {
			for (Class<? extends Reason> reason : type.getValue().reasons()) {
				String key = RequirementWording.DENIED + "." + Reason.key(type.getKey(), reason);
				if (phrases.find("", key).isEmpty())
					Logger.warn("No phrase is named %s, so players denied for it are told the generic notice", key);
			}
		}

		for (Map.Entry<String, List<String>> problem : phrases.unresolved().entrySet())
			Logger.warn("The phrase %s refers to %s, which no phrases file has", problem.getKey(), String.join(", ", problem.getValue()));
	}
}
