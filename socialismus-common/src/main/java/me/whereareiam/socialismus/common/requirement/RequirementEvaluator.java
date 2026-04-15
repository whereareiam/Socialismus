package me.whereareiam.socialismus.common.requirement;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;
import me.whereareiam.socialismus.service.requirement.RequirementValidation;

import java.util.Map;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class RequirementEvaluator implements RequirementEvaluatorService {
	private final RequirementRegistry requirementRegistry;

	private boolean isRequirementMet(Map.Entry<String, ? extends Requirement> entry, SocialismusPlayer player) {
		RequirementValidation checker = requirementRegistry.get(entry.getKey());
		if (checker == null) {
			Logger.warn("No requirement validation registered for: " + entry.getKey());
			return false;
		}

		return checker.check(entry.getValue(), player);
	}

	@Override
	public boolean check(RequirementGroup group, SocialismusPlayer player) {
		if (group == null || group.getGroups().isEmpty()) return true;

		return switch (group.getOperator()) {
			case AND -> checkAnd(group, player);
			case OR -> checkOr(group, player);
			case XOR -> checkXor(group, player);
			case NOT -> checkNot(group, player);
			case NAND -> checkNand(group, player);
			case NOR -> checkNor(group, player);
		};
	}

	private boolean checkAnd(RequirementGroup group, SocialismusPlayer player) {
		Logger.debug("Checking AND group for player " + player.getUsername());
		for (Map.Entry<String, ? extends Requirement> entry : group.getGroups().entrySet())
			if (!isRequirementMet(entry, player))
				return false;

		return true;
	}

	private boolean checkOr(RequirementGroup group, SocialismusPlayer player) {
		Logger.debug("Checking OR group for player " + player.getUsername());
		for (Map.Entry<String, ? extends Requirement> entry : group.getGroups().entrySet())
			if (isRequirementMet(entry, player))
				return true;

		return false;
	}

	private boolean checkXor(RequirementGroup group, SocialismusPlayer player) {
		boolean oneMet = false;
		Logger.debug("Checking XOR group for player " + player.getUsername());
		for (Map.Entry<String, ? extends Requirement> entry : group.getGroups().entrySet())
			if (isRequirementMet(entry, player)) {
				if (oneMet) return false;
				oneMet = true;
			}

		return oneMet;
	}

	private boolean checkNot(RequirementGroup group, SocialismusPlayer player) {
		Logger.debug("Checking NOT group for player " + player.getUsername());
		for (Map.Entry<String, ? extends Requirement> entry : group.getGroups().entrySet())
			if (isRequirementMet(entry, player))
				return false;

		return true;
	}

	private boolean checkNand(RequirementGroup group, SocialismusPlayer player) {
		return !checkAnd(group, player);
	}

	private boolean checkNor(RequirementGroup group, SocialismusPlayer player) {
		return !checkOr(group, player);
	}
}