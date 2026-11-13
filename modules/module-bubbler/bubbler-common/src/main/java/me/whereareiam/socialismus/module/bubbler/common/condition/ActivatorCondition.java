package me.whereareiam.socialismus.module.bubbler.common.condition;

import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.type.ActivatorType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * {@code bubbler:activator: [COMMAND]}: holds when the bubble was raised by one of the activators listed.
 */
public final class ActivatorCondition implements ConditionType.Single<BubbleMessage, ActivatorCondition.Settings> {
	@Override
	public @NotNull String name() {
		return "activator";
	}

	@Override
	public @NotNull Class<BubbleMessage> subject() {
		return BubbleMessage.class;
	}

	@Override
	public @NotNull Class<Settings> settings() {
		return Settings.class;
	}

	@Override
	public @NotNull Outcome holds(@NotNull BubbleMessage message, @NotNull Settings settings) {
		return Outcome.when(settings.activators().contains(message.getActivatorType()));
	}

	/**
	 * The condition's settings.
	 *
	 * @param activators the activators that may have raised the bubble
	 */
	public record Settings(@NotNull List<ActivatorType> activators) {
	}
}
