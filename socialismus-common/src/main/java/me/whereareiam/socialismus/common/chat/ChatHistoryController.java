package me.whereareiam.socialismus.common.chat;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.input.chat.ChatHistoryService;
import me.whereareiam.socialismus.api.input.container.ChatHistoryContainerService;
import me.whereareiam.socialismus.api.model.chat.ChatSettings;
import me.whereareiam.socialismus.api.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.common.chat.broadcast.ChatBroadcaster;
import net.kyori.adventure.text.Component;

import java.util.List;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ChatHistoryController implements ChatHistoryService {
	private final ChatHistoryContainerService chatHistoryContainer;
	private final Provider<ChatSettings> chatSettings;
	private final ChatBroadcaster chatBroadcaster;

	@Override
	public boolean removeMessage(int id) {
		boolean removed = chatHistoryContainer.removeMessage(id);
		if (removed) sendChatHistory();

		return removed;
	}

	@Override
	public int removeMessages(int amount) {
		int count = chatHistoryContainer.removeMessages(amount);
		if (count > 0) sendChatHistory();

		return count;
	}

	@Override
	public int removeMessages(String username) {
		List<FormattedChatMessage> messages = chatHistoryContainer.getMessages(username);

		int count = messages.size();
		messages.forEach(message -> chatHistoryContainer.removeMessage(message.getId()));

		if (count > 0) sendChatHistory();

		return count;
	}

	private void sendChatHistory() {
		Component filler = Component.empty();
		for (int i = 0; i < chatSettings.get().getHistory().getFillerSize(); i++)
			filler = filler.append(Component.newline());

		Component finalFiller = filler;
		chatHistoryContainer.getMessages().forEach(m -> {
			m.getSender().sendMessage(finalFiller);
			chatBroadcaster.broadcast(m);
		});
	}
}