package me.whereareiam.socialismus.common.requirement;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementDenial;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.model.requirement.RequirementResult;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;
import me.whereareiam.socialismus.service.requirement.RequirementValidation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class RequirementEvaluator implements RequirementEvaluatorService {
	private final RequirementRegistry requirementRegistry;

	@Override
	public @NotNull RequirementResult evaluate(@Nullable RequirementGroup group, @NotNull SocialismusPlayer player) {
		if (group == null || group.getGroups() == null || group.getGroups().isEmpty()) return RequirementResult.passed();

		Logger.debug("Checking %s group for player %s", group.getOperator(), player.getUsername());
		RequirementResult result = switch (group.getOperator()) {
			case AND -> evaluateAnd(group, player);
			case OR -> evaluateOr(group, player);
			case XOR -> evaluateXor(group, player);
			case NOT, NOR -> evaluateNone(group, player);
			case NAND -> evaluateNotAll(group, player);
		};
		if (result.isPassed() || group.getReason() == null) return result;
		// A blank reason says the group tells nothing, so neither do its requirements.
		if (group.getReason().isBlank()) return RequirementResult.denied();

		return RequirementResult.denied(result.getDenial(), group.getReason());
	}

	private RequirementResult evaluate(Map.Entry<String, ? extends Requirement> entry, SocialismusPlayer player) {
		RequirementValidation validation = requirementRegistry.get(entry.getKey());
		if (validation == null) {
			Logger.warn("No requirement validation registered for: " + entry.getKey());
			return RequirementResult.denied();
		}

		return validation.evaluate(entry.getValue(), player);
	}

	private RequirementResult evaluateAnd(RequirementGroup group, SocialismusPlayer player) {
		for (Map.Entry<String, ? extends Requirement> entry : group.getGroups().entrySet()) {
			RequirementResult result = evaluate(entry, player);
			if (result.isDenied()) return result;
		}

		return RequirementResult.passed();
	}

	private RequirementResult evaluateOr(RequirementGroup group, SocialismusPlayer player) {
		RequirementDenial denial = null;
		for (Map.Entry<String, ? extends Requirement> entry : group.getGroups().entrySet()) {
			RequirementResult result = evaluate(entry, player);
			if (result.isPassed()) return result;
			if (denial == null) denial = result.getDenial();
		}

		return RequirementResult.denied(denial);
	}

	private RequirementResult evaluateXor(RequirementGroup group, SocialismusPlayer player) {
		boolean oneMet = false;
		RequirementDenial denial = null;
		for (Map.Entry<String, ? extends Requirement> entry : group.getGroups().entrySet()) {
			RequirementResult result = evaluate(entry, player);
			if (result.isDenied()) {
				if (denial == null) denial = result.getDenial();
				continue;
			}

			// A second met requirement denies the group, and no requirement can say why.
			if (oneMet) return RequirementResult.denied();
			oneMet = true;
		}

		return oneMet ? RequirementResult.passed() : RequirementResult.denied(denial);
	}

	private RequirementResult evaluateNone(RequirementGroup group, SocialismusPlayer player) {
		for (Map.Entry<String, ? extends Requirement> entry : group.getGroups().entrySet())
			if (evaluate(entry, player).isPassed())
				return RequirementResult.denied();

		return RequirementResult.passed();
	}

	private RequirementResult evaluateNotAll(RequirementGroup group, SocialismusPlayer player) {
		return evaluateAnd(group, player).isPassed() ? RequirementResult.denied() : RequirementResult.passed();
	}
}
