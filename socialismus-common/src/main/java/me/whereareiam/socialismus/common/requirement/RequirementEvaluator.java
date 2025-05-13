package me.whereareiam.socialismus.common.requirement;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.input.requirement.RequirementEvaluatorService;
import me.whereareiam.socialismus.api.input.requirement.RequirementValidation;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.requirement.Requirement;
import me.whereareiam.socialismus.api.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.api.type.requirement.RequirementType;

import java.util.Map;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class RequirementEvaluator implements RequirementEvaluatorService {
	private final RequirementRegistry requirementRegistry;

	private boolean isRequirementMet(Map.Entry<RequirementType, ? extends Requirement> entry, DummyPlayer dummyPlayer) {
		RequirementValidation checker = requirementRegistry.get(entry.getKey());
		if (checker == null)
			return false;

		return checker.check(entry.getValue(), dummyPlayer);
	}

	@Override
	public boolean check(RequirementGroup group, DummyPlayer dummyPlayer) {
		if (group == null || group.getGroups().isEmpty()) return true;

		return switch (group.getOperator()) {
			case AND -> checkAnd(group, dummyPlayer);
			case OR -> checkOr(group, dummyPlayer);
			case XOR -> checkXor(group, dummyPlayer);
			case NOT -> checkNot(group, dummyPlayer);
			case NAND -> checkNand(group, dummyPlayer);
			case NOR -> checkNor(group, dummyPlayer);
		};
	}

	private boolean checkAnd(RequirementGroup group, DummyPlayer dummyPlayer) {
		Logger.debug("Checking AND group for player " + dummyPlayer.getUsername());
		for (Map.Entry<RequirementType, ? extends Requirement> entry : group.getGroups().entrySet())
			if (!isRequirementMet(entry, dummyPlayer))
				return false;

		return true;
	}

	private boolean checkOr(RequirementGroup group, DummyPlayer dummyPlayer) {
		Logger.debug("Checking OR group for player " + dummyPlayer.getUsername());
		for (Map.Entry<RequirementType, ? extends Requirement> entry : group.getGroups().entrySet())
			if (isRequirementMet(entry, dummyPlayer))
				return true;

		return false;
	}

	private boolean checkXor(RequirementGroup group, DummyPlayer dummyPlayer) {
		boolean oneMet = false;
		Logger.debug("Checking XOR group for player " + dummyPlayer.getUsername());
		for (Map.Entry<RequirementType, ? extends Requirement> entry : group.getGroups().entrySet())
			if (isRequirementMet(entry, dummyPlayer)) {
				if (oneMet) return false;
				oneMet = true;
			}

		return oneMet;
	}

	private boolean checkNot(RequirementGroup group, DummyPlayer dummyPlayer) {
		Logger.debug("Checking NOT group for player " + dummyPlayer.getUsername());
		for (Map.Entry<RequirementType, ? extends Requirement> entry : group.getGroups().entrySet())
			if (isRequirementMet(entry, dummyPlayer))
				return false;

		return true;
	}

	private boolean checkNand(RequirementGroup group, DummyPlayer dummyPlayer) {
		return !checkAnd(group, dummyPlayer);
	}

	private boolean checkNor(RequirementGroup group, DummyPlayer dummyPlayer) {
		return !checkOr(group, dummyPlayer);
	}
}