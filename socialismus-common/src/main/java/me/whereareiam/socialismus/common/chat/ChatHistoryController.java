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
import me.whereareiam.socialismus.model.chat.history.HistorySelection;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.service.chat.ChatHistoryService;
import me.whereareiam.socialismus.service.container.ChatHistoryContainerService;
import me.whereareiam.socialismus.type.BroadcastTarget;
import me.whereareiam.socialismus.util.EventUtil;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ChatHistoryController implements ChatHistoryService {
	private final ChatHistoryContainerService chatHistoryContainer;
	private final Provider<ChatSettings> chatSettings;
	private final ChatBroadcaster chatBroadcaster;
	private final PlatformInteractor interactor;

	@Override
	public int remove(@NotNull HistorySelection selection) {
		int removed = switch (selection) {
			case HistorySelection.Message message -> chatHistoryContainer.removeMessage(message.id()) ? 1 : 0;
			case HistorySelection.Oldest oldest -> chatHistoryContainer.removeMessages(oldest.amount());
			case HistorySelection.OfPlayer player -> removeAll(chatHistoryContainer.getMessages(player.player().getUniqueId()));
			case HistorySelection.ByName name -> removeAll(chatHistoryContainer.getMessages(name.username()));
		};
		if (removed == 0)
			return 0;

		EventUtil.callEvent(event(selection), this::sendChatHistory);

		return removed;
	}

	private int removeAll(List<FormattedChatMessage> messages) {
		return (int) messages.stream()
				.filter(message -> chatHistoryContainer.removeMessage(message.getId()))
				.count();
	}

	private static ChatHistoryRemoveEvent event(HistorySelection selection) {
		return switch (selection) {
			case HistorySelection.Message message -> new ChatHistoryRemoveByIdEvent(message);
			case HistorySelection.Oldest oldest -> new ChatHistoryRemoveByAmountEvent(oldest);
			case HistorySelection.OfPlayer player -> new ChatHistoryRemoveByPlayerEvent(player);
			case HistorySelection.ByName name -> new ChatHistoryRemoveByPlayerEvent(name);
		};
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