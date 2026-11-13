package me.whereareiam.socialismus.common.chat.worker.base;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.service.chat.ChatConditions;
import me.whereareiam.socialismus.service.style.TextStyle;
import me.whereareiam.socialismus.util.ComponentUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Reads what a sender wrote with the style their chat gives them, once the chat is chosen. A message relayed from
 * another server was read where it was written, by the server that knows the sender, so it stays as it came.
 */
@Singleton
public class StyleReader {
	private final ChatConditions chatConditions;

	@Inject
	public StyleReader(@NotNull WorkerProcessor<ChatMessage> workerProcessor, @NotNull ChatConditions chatConditions) {
		this.chatConditions = chatConditions;

		workerProcessor.addWorker(new Worker<>(this::read, 60, true, false));
	}

	ChatMessage read(ChatMessage message) {
		if (message.isRelayed() || message.getChat() == null) return message;
		if (message.getChat().getParameters().getStyles().isEmpty()) return message;

		String text = ComponentUtil.toPlain(message.getContent());
		TextStyle style = chatConditions.of(message.getChat())
				.map(places -> places.style(message.getSender()))
				.orElse(TextStyle.PLAIN);
		message.setContent(style.read(text));

		return message;
	}
}
