package me.whereareiam.socialismus.module.bubbler.common.worker.container;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.type.ActivatorType;
import me.whereareiam.socialismus.module.bubbler.common.condition.BubbleConditions;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.service.style.TextStyle;
import me.whereareiam.socialismus.util.ComponentUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Reads the text of a bubble raised by {@code /socialismus bubble} with the style its bubble gives the sender, once
 * the bubble is chosen. A bubble raised by a chat message keeps the message as its chat read it.
 */
@Singleton
public class StyleReader {
	private final BubbleConditions conditions;

	@Inject
	public StyleReader(@NotNull WorkerProcessor<BubbleMessage> workerProcessor, @NotNull BubbleConditions conditions) {
		this.conditions = conditions;

		workerProcessor.addWorker(new Worker<>(this::read, 125, false, false));
	}

	BubbleMessage read(BubbleMessage message) {
		if (message.getActivatorType() != ActivatorType.COMMAND || message.getBubble() == null) return message;

		List<String> accepted = message.getBubble().getStyles();
		if (accepted == null || accepted.isEmpty()) return message;

		String text = ComponentUtil.toPlain(message.getContent());
		TextStyle style = conditions.of(message.getBubble())
				.map(places -> places.style(message.getSender()))
				.orElse(TextStyle.PLAIN);
		message.setContent(style.read(text));

		return message;
	}
}
