package me.whereareiam.socialismus.common.chat;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.common.chat.broadcast.ChatBroadcaster;
import me.whereareiam.socialismus.event.chat.history.ChatHistoryRemoveByAmountEvent;
import me.whereareiam.socialismus.event.chat.history.ChatHistoryRemoveByIdEvent;
import me.whereareiam.socialismus.event.chat.history.ChatHistoryRemoveByPlayerEvent;
import me.whereareiam.socialismus.event.chat.history.ChatHistoryRemoveEvent;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.service.chat.ChatHistoryService;
import me.whereareiam.socialismus.service.container.ChatHistoryContainerService;
import me.whereareiam.socialismus.type.BroadcastTarget;
import me.whereareiam.socialismus.util.EventUtil;
import net.kyori.adventure.text.Component;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ChatHistoryController implements ChatHistoryService {
	private final ChatHistoryContainerService chatHistoryContainer;
	private final Provider<ChatSettings> chatSettings;
	private final ChatBroadcaster chatBroadcaster;
	private final PlatformInteractor interactor;

	@Override
	public boolean removeMessage(int id) {
		return removeMessage(id, true);
	}

	@Override
	public boolean removeMessage(int id, boolean callEvent) {
		return processRemoval(() -> chatHistoryContainer.removeMessage(id) ? 1 : 0,
				() -> new ChatHistoryRemoveByIdEvent(id), callEvent) > 0;
	}

	@Override
	public int removeMessages(int amount) {
		return removeMessages(amount, true);
	}

	@Override
	public int removeMessages(int amount, boolean callEvent) {
		return processRemoval(() -> chatHistoryContainer.removeMessages(amount),
				() -> new ChatHistoryRemoveByAmountEvent(amount), callEvent);
	}

	@Override
	public int removeMessages(String username) {
		return removeMessages(username, true);
	}

	@Override
	public int removeMessages(String username, boolean callEvent) {
		return processRemoval(() -> (int) chatHistoryContainer.getMessages(username).stream()
						.filter(message -> chatHistoryContainer.removeMessage(message.getId()))
						.count(),
				() -> new ChatHistoryRemoveByPlayerEvent(username), callEvent);
	}

	private int processRemoval(
			IntSupplier removalFunction,
			Supplier<ChatHistoryRemoveEvent> eventSupplier,
			boolean callEvent
	) {
		int removed = removalFunction.getAsInt();
		if (removed == 0)
			return 0;

		if (callEvent)
			EventUtil.callEvent(eventSupplier.get(), this::sendChatHistory);
		else
			sendChatHistory();

		return removed;
	}

	private void sendChatHistory() {
		Component filler = Component.empty();
		for (int i = 0; i < chatSettings.get().getHistory().getFillerSize(); i++)
			filler = filler.append(Component.newline());

		Component finalFiller = filler;
		interactor.broadcast(finalFiller, BroadcastTarget.PLAYERS);
		chatHistoryContainer.getMessages()
				.forEach(chatBroadcaster::broadcast);
	}
}