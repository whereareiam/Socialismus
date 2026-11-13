package me.whereareiam.socialismus.module.bubbler.common.worker.container;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.type.ActivatorType;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.service.style.StyleService;
import me.whereareiam.socialismus.util.ComponentUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Reads the text of a bubble raised by {@code /socialismus bubble} with the styles its bubble accepts, once the bubble
 * is chosen. A bubble raised by a chat message keeps the message as its chat read it.
 */
@Singleton
public class StyleReader {
	private final StyleService styles;

	@Inject
	public StyleReader(@NotNull WorkerProcessor<BubbleMessage> workerProcessor, @NotNull StyleService styles) {
		this.styles = styles;

		workerProcessor.addWorker(new Worker<>(this::read, 125, false, false));
	}

	BubbleMessage read(BubbleMessage message) {
		if (message.getActivatorType() != ActivatorType.COMMAND || message.getBubble() == null) return message;

		List<String> accepted = message.getBubble().getStyles();
		if (accepted == null || accepted.isEmpty()) return message;

		String text = ComponentUtil.toPlain(message.getContent());
		message.setContent(styles.read(message.getSender(), text, accepted));

		return message;
	}
}
