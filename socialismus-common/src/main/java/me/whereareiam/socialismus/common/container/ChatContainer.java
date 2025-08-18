package me.whereareiam.socialismus.common.container;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.Reloadable;
import me.whereareiam.socialismus.api.input.container.ChatContainerService;
import me.whereareiam.socialismus.api.input.event.chat.ChatAddedEvent;
import me.whereareiam.socialismus.api.input.event.chat.ChatUpdatedEvent;
import me.whereareiam.socialismus.api.input.registry.Registry;
import me.whereareiam.socialismus.api.model.chat.Chat;
import me.whereareiam.socialismus.api.model.chat.ChatSettings;
import me.whereareiam.socialismus.api.model.chat.InternalChat;
import me.whereareiam.socialismus.api.type.chat.TriggerType;
import me.whereareiam.socialismus.api.model.chat.trigger.SymbolChatTrigger;
import me.whereareiam.socialismus.api.util.EventUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ChatContainer implements ChatContainerService, Reloadable {
	private final Provider<ChatSettings> chatSettings;
	private final Provider<List<Chat>> chatsProvider;

	private final ConcurrentHashMap<String, InternalChat> chats = new ConcurrentHashMap<>();

	@Inject
	public ChatContainer(
			@Named("chats") Provider<List<Chat>> chatsProvider,
			Provider<ChatSettings> chatSettings,
			Registry<Reloadable> registry
	) {
		this.chatsProvider = chatsProvider;
		this.chatSettings = chatSettings;

		registry.register(this);
		chatsProvider.get().forEach(this::addChat);
	}

	@Override
	public void addChat(InternalChat chat) {
		if (chat.getId() == null || chat.getId().isEmpty()) {
			Logger.debug("Chat " + chat.getId() + " has no id");
			return;
		}

		if (chats.containsKey(chat.getId())) {
			Logger.debug("Chat " + chat.getId() + " already exists, but was tried to be added again");
			return;
		}

		EventUtil.callEvent(new ChatAddedEvent(chat, false), () -> chats.put(chat.getId(), chat));
	}

	@Override
	public void addChat(Chat chat) {
		addChat(InternalChat.from(chat));
	}

	@Override
	public void updateChat(String id, Chat chat) {
		EventUtil.callEvent(new ChatUpdatedEvent(chats.get(id), chat, false), () -> chats.put(chat.getId(), InternalChat.from(chat)));
	}

	@Override
	public boolean hasChat(String id) {
		return chats.containsKey(id);
	}

	@Override
	public boolean hasChatBySymbol(String symbol) {
		return chats.values().stream()
				.filter(chat -> chat.getTriggers() != null)
				.anyMatch(chat -> chat.getTriggers().stream()
						.anyMatch(t -> t.getType() == TriggerType.SYMBOL && symbol.equals(((SymbolChatTrigger) t).getSymbol()))
				);
	}

	@Override
	public Optional<InternalChat> getChat(String id) {
		return chats.get(id) == null
				? Optional.empty()
				: Optional.of(chats.get(id));
	}

	@Override
	public List<InternalChat> getChatBySymbol(String symbol) {
		return chats.values().stream()
				.filter(chat -> !chat.getId().equals(chatSettings.get().getFallback().getChatId()))
				.filter(chat -> chat.getTriggers() != null && chat.getTriggers().stream()
						.anyMatch(t -> t.getType() == TriggerType.SYMBOL && symbol.equals(((SymbolChatTrigger) t).getSymbol())))
				.sorted(Comparator.comparingInt(Chat::getPriority).reversed())
				.toList();
	}

	@Override
	public Set<InternalChat> getChats() {
		return new HashSet<>(chats.values());
	}

	@Override
	public void reload() {
		chats.clear();
		chatsProvider.get().forEach(this::addChat);
	}
}
