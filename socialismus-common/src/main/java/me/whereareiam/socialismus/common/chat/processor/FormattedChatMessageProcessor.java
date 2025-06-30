package me.whereareiam.socialismus.common.chat.processor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.input.WorkerProcessor;
import me.whereareiam.socialismus.api.model.Worker;
import me.whereareiam.socialismus.api.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.api.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.api.model.config.Settings;
import net.kyori.adventure.text.Component;

import java.util.LinkedList;

@Getter
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class FormattedChatMessageProcessor implements WorkerProcessor<FormattedChatMessage> {
	private final LinkedList<Worker<FormattedChatMessage>> workers = new LinkedList<>();
	private final Provider<Settings> settings;

	public FormattedChatMessage process(ChatMessage chatMessage) {
		FormattedChatMessage formattedChatMessage = FormattedChatMessage.from(
				chatMessage,
				Component.empty(),
				settings.get().getMisc().isVanillaSending()
		);

		for (Worker<FormattedChatMessage> worker : workers) {
			formattedChatMessage = worker.getFunction().apply(formattedChatMessage);

			if (formattedChatMessage.isCancelled()) break;
		}

		return formattedChatMessage;
	}

	@Override
	public void addWorker(Worker<FormattedChatMessage> worker) {
		if (workers.stream().noneMatch(w -> w.getPriority() == worker.getPriority())) {
			workers.add(worker);
			workers.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()));
		}
	}

	@Override
	public boolean removeWorker(Worker<FormattedChatMessage> worker) {
		if (!worker.isRemovable()) return false;
		return workers.remove(worker);
	}
}
