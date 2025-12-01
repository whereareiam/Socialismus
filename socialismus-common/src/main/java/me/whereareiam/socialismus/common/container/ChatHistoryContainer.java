package me.whereareiam.socialismus.common.container;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.input.container.ChatHistoryContainerService;
import me.whereareiam.socialismus.input.event.chat.history.MessageAddedEvent;
import me.whereareiam.socialismus.input.event.chat.history.MessageRemovedEvent;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.util.EventUtil;

import java.util.*;

@Singleton
public class ChatHistoryContainer implements ChatHistoryContainerService {
	private final Map<Integer, FormattedChatMessage> chatHistory;

	@Inject
	public ChatHistoryContainer(Provider<ChatSettings> chatSettings) {
		this.chatHistory = new LinkedHashMap<>() {
			@Override
			protected boolean removeEldestEntry(Map.Entry<Integer, FormattedChatMessage> eldest) {
				return size() > chatSettings.get().getHistory().getHistorySize();
			}
		};
	}

	@Override
	public void addMessage(int id, FormattedChatMessage message) {
		EventUtil.callEvent(new MessageAddedEvent(message, message.isCancelled()), () -> chatHistory.put(id, message));
	}

	@Override
	public boolean removeMessage(int id) {
		FormattedChatMessage chatMessage = chatHistory.get(id);
		return EventUtil.callEvent(new MessageRemovedEvent(chatMessage, chatMessage.isCancelled()), () -> chatHistory.remove(id));
	}

	@Override
	public int removeMessages(int amount) {
		int removed = 0;
		Iterator<Map.Entry<Integer, FormattedChatMessage>> iterator = chatHistory.entrySet().iterator();

		while (iterator.hasNext() && removed < amount) {
			Map.Entry<Integer, FormattedChatMessage> entry = iterator.next();
			FormattedChatMessage chatMessage = entry.getValue();
			if (EventUtil.callEvent(new MessageRemovedEvent(chatMessage, chatMessage.isCancelled()), iterator::remove))
				removed++;
		}

		return removed;
	}

	@Override
	public Optional<FormattedChatMessage> getMessage(int id) {
		return chatHistory.containsKey(id) ? Optional.of(chatHistory.get(id)) : Optional.empty();
	}

	@Override
	public List<FormattedChatMessage> getMessages(String username) {
		return chatHistory.values().parallelStream()
				.filter(message -> message.getSender().getUsername().equals(username))
				.toList();
	}

	@Override
	public List<FormattedChatMessage> getMessages(UUID uniqueId) {
		return chatHistory.values().parallelStream()
				.filter(message -> message.getSender().getUniqueId().equals(uniqueId))
				.toList();
	}

	@Override
	public List<FormattedChatMessage> getMessages() {
		return new ArrayList<>(chatHistory.values());
	}
}
