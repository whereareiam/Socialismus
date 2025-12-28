package me.whereareiam.socialismus.common.requirement.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.integration.PlaceholderIntegration;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementKey;
import me.whereareiam.socialismus.model.requirement.type.PlaceholderRequirement;
import me.whereareiam.socialismus.registry.base.ExtendedRegistry;
import me.whereareiam.socialismus.service.requirement.RequirementValidation;

import java.util.List;
import java.util.Set;

@Singleton
public class PlaceholderRequirementValidation implements RequirementValidation {
	private final Set<Integration> integrations;

	@Inject
	public PlaceholderRequirementValidation(
			ExtendedRegistry<RequirementKey<?>, RequirementValidation> registry,
			Set<Integration> integrations
	) {
		this.integrations = integrations;

		registry.register(Constants.Requirements.PLACEHOLDER, this);
	}

	@Override
	public boolean check(Requirement requirement, SocialismusPlayer player) {
		PlaceholderIntegration resolver = findPlaceholderIntegration();
		if (resolver == null || !(requirement instanceof PlaceholderRequirement pr))
			return false;

		Logger.debug("Checking placeholder requirement for player " + player.getUsername());
		return checkCondition(pr, resolver, player);
	}

	private PlaceholderIntegration findPlaceholderIntegration() {
		return integrations.stream()
				.filter(integration -> integration instanceof PlaceholderIntegration)
				.filter(Integration::isAvailable)
				.map(integration -> (PlaceholderIntegration) integration)
				.findFirst()
				.orElse(null);
	}

	private boolean checkCondition(PlaceholderRequirement pr, PlaceholderIntegration resolver, SocialismusPlayer player) {
		List<String> placeholders = pr.getPlaceholders();
		String[] expectedValues = pr.getExpected().split("\\|");

		for (String placeholder : placeholders) {
			String resolvedPlaceholder = resolver.resolve(player.getUniqueId(), placeholder);

			for (String expected : expectedValues) {
				boolean result = switch (pr.getCondition()) {
					case EQUALS -> {
						boolean equals = resolvedPlaceholder.equals(expected);
						Logger.debug("EQUALS comparison: '" + resolvedPlaceholder + "' == '" + expected + "' = " + equals);
						yield equals;
					}
					case GREATER_THAN, LESS_THAN, GREATER_THAN_OR_EQUALS, LESS_THAN_OR_EQUALS -> {
						boolean comparison = compareNumericValues(pr, resolvedPlaceholder, expected);
						Logger.debug("Numeric comparison " + pr.getCondition() + " for values: " + resolvedPlaceholder + " " + pr.getCondition() + " " + expected + " = " + comparison);
						yield String.valueOf(comparison).equals(expected);
					}
					default -> false;
				};

				if (result) {
					Logger.debug("Found matching condition for player " + player.getUsername());
					return true;
				}
			}
		}

		Logger.debug("No matching conditions found for player " + player.getUsername());
		return false;
	}

	private boolean compareNumericValues(PlaceholderRequirement pr, String formattedValue, String expected) {
		try {
			double value = Double.parseDouble(formattedValue);
			double expectedValue = Double.parseDouble(expected);

			boolean result = switch (pr.getCondition()) {
				case GREATER_THAN -> value > expectedValue;
				case LESS_THAN -> value < expectedValue;
				case GREATER_THAN_OR_EQUALS -> value >= expectedValue;
				case LESS_THAN_OR_EQUALS -> value <= expectedValue;
				default -> false;
			};

			Logger.debug("Numeric comparison result: " + result + " for values: " + value + " " + pr.getCondition() + " " + expectedValue);
			return result;
		} catch (NumberFormatException e) {
			Logger.debug("Failed to parse numeric values: " + formattedValue + " " + pr.getCondition() + " " + expected);
			return false;
		}
	}
}