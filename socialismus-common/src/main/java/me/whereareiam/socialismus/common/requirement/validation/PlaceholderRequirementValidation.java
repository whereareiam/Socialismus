package me.whereareiam.socialismus.common.requirement.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Logger;
import me.whereareiam.socialismus.input.requirement.RequirementValidation;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.type.PlaceholderRequirement;
import me.whereareiam.socialismus.output.integration.Integration;
import me.whereareiam.socialismus.output.integration.PlaceholderResolverIntegration;
import me.whereareiam.socialismus.registry.ExtendedRegistry;
import me.whereareiam.socialismus.type.requirement.RequirementType;

import java.util.List;
import java.util.Set;

@Singleton
public class PlaceholderRequirementValidation implements RequirementValidation {
	private final Set<Integration> integrations;

	@Inject
	public PlaceholderRequirementValidation(
			ExtendedRegistry<RequirementType, RequirementValidation> registry,
			Set<Integration> integrations
	) {
		this.integrations = integrations;

		registry.register(RequirementType.PLACEHOLDER, this);
	}

	@Override
	public boolean check(Requirement requirement, SocialismusPlayer player) {
		PlaceholderResolverIntegration resolver = findPlaceholderResolverIntegration();
		if (resolver == null || !(requirement instanceof PlaceholderRequirement pr))
			return false;

		Logger.debug("Checking placeholder requirement for player " + player.getUsername());
		return checkCondition(pr, resolver, player);
	}

	private PlaceholderResolverIntegration findPlaceholderResolverIntegration() {
		return integrations.stream()
				.filter(integration -> integration instanceof PlaceholderResolverIntegration)
				.map(integration -> (PlaceholderResolverIntegration) integration)
				.findFirst()
				.orElse(null);
	}

	private boolean checkCondition(PlaceholderRequirement pr, PlaceholderResolverIntegration resolver, SocialismusPlayer player) {
		List<String> placeholders = pr.getPlaceholders();
		String[] expectedValues = pr.getExpected().split("\\|");

		for (String placeholder : placeholders) {
			String resolvedPlaceholder = resolver.format(player, placeholder);

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