package me.whereareiam.socialismus.common.chat.worker.base;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.service.chat.ChatConditionService;
import me.whereareiam.socialismus.util.ComponentUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Reads what a sender wrote with the style their chat gives them, once the chat is chosen. A message of a sender
 * hosted on another server was read there, by the server that can check the sender's conditions, so it stays as it
 * came.
 */
@Singleton
public class StyleReader {
	private final ChatConditionService conditions;

	@Inject
	public StyleReader(@NotNull WorkerProcessor<ChatMessage> workerProcessor, @NotNull ChatConditionService conditions) {
		this.conditions = conditions;

		workerProcessor.addWorker(new Worker<>(this::read, 60, true, false));
	}

	ChatMessage read(ChatMessage message) {
		if (!message.getSender().isHostedHere() || message.getChat() == null) return message;
		if (message.getChat().getParameters().getStyles().isEmpty()) return message;

		String text = ComponentUtil.toPlain(message.getContent());
		message.setContent(conditions.style(message.getChat(), message.getSender()).read(text));

		return message;
	}
}
