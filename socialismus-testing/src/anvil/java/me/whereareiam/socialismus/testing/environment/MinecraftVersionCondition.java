package me.whereareiam.socialismus.testing.environment;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

/**
 * Skips a journey whose {@link MinecraftVersions} leave out the Minecraft version of this run.
 */
final class MinecraftVersionCondition implements ExecutionCondition {
	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
		return context.getElement()
				.flatMap(element -> AnnotationSupport.findAnnotation(element, MinecraftVersions.class))
				.map(MinecraftVersionCondition::evaluate)
				.orElse(ConditionEvaluationResult.enabled("No Minecraft versions declared"));
	}

	private static ConditionEvaluationResult evaluate(MinecraftVersions versions) {
		if (!versions.since().isBlank() && !Versions.atLeast(versions.since()))
			return ConditionEvaluationResult.disabled("Runs from Minecraft " + versions.since() + ", not at " + Versions.MINECRAFT);
		if (!versions.before().isBlank() && Versions.atLeast(versions.before()))
			return ConditionEvaluationResult.disabled("Runs before Minecraft " + versions.before() + ", not at " + Versions.MINECRAFT);

		return ConditionEvaluationResult.enabled("Runs at Minecraft " + Versions.MINECRAFT);
	}
}
