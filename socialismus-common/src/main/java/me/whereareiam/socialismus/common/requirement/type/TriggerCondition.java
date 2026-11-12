package me.whereareiam.socialismus.common.requirement.type;

import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.socialismus.model.chat.ChatTrigger;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.type.chat.TriggerType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * {@code trigger: [SYMBOL]}: holds when the message reached its chat through one of the kinds of trigger listed. A
 * message whose trigger is not chosen yet has none.
 */
public final class TriggerCondition implements ConditionType.Single<ChatMessage, TriggerCondition.Settings> {
	@Override
	public @NotNull String name() {
		return "trigger";
	}

	@Override
	public @NotNull Class<ChatMessage> subject() {
		return ChatMessage.class;
	}

	@Override
	public @NotNull Class<Settings> settings() {
		return Settings.class;
	}

	@Override
	public @NotNull Outcome holds(@NotNull ChatMessage message, @NotNull Settings settings) {
		ChatTrigger trigger = message.getTrigger();

		return Outcome.when(trigger != null && settings.triggers().contains(trigger.getType()));
	}

	/**
	 * The condition's settings.
	 *
	 * @param triggers the kinds of trigger the message may have come through
	 */
	public record Settings(@NotNull List<TriggerType> triggers) {
	}
}
