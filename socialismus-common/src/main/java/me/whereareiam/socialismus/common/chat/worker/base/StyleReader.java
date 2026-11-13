package me.whereareiam.socialismus.common.chat.worker.base;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.service.style.StyleService;
import me.whereareiam.socialismus.util.ComponentUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Reads what a sender wrote with the styles their chat accepts, once the chat is chosen. A message relayed from
 * another server was read where it was written, by the server that knows the sender, so it stays as it came.
 */
@Singleton
public class StyleReader {
	private final StyleService styles;

	@Inject
	public StyleReader(@NotNull WorkerProcessor<ChatMessage> workerProcessor, @NotNull StyleService styles) {
		this.styles = styles;

		workerProcessor.addWorker(new Worker<>(this::read, 60, true, false));
	}

	ChatMessage read(ChatMessage message) {
		if (message.isRelayed() || message.getChat() == null) return message;

		List<String> accepted = message.getChat().getParameters().getStyles();
		if (accepted == null || accepted.isEmpty()) return message;

		String text = ComponentUtil.toPlain(message.getContent());
		message.setContent(styles.read(message.getSender(), text, accepted));

		return message;
	}
}
