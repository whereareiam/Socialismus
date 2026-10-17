package me.whereareiam.socialismus.common.container;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.chat.ChatAddedEvent;
import me.whereareiam.socialismus.event.chat.ChatUpdatedEvent;
import me.whereareiam.socialismus.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.InternalChat;
import me.whereareiam.socialismus.model.chat.trigger.SymbolChatTrigger;
import me.whereareiam.socialismus.service.container.ChatContainerService;
import me.whereareiam.socialismus.type.chat.TriggerType;
import me.whereareiam.socialismus.util.EventUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds the chats of the chats document. They are loaded when the runtime starts and built again from the document
 * after a reload.
 */
@Singleton
public class ChatContainer implements ChatContainerService, EventListener {
	private final Provider<ChatSettings> chatSettings;
	private final Provider<List<Chat>> chatsProvider;

	private final ConcurrentHashMap<String, InternalChat> chats = new ConcurrentHashMap<>();

	@Inject
	public ChatContainer(
			@Named("chats") Provider<List<Chat>> chatsProvider,
			Provider<ChatSettings> chatSettings,
			EventManager eventManager
	) {
		this.chatsProvider = chatsProvider;
		this.chatSettings = chatSettings;

		eventManager.register(this);
	}

	/**
	 * Replaces the held chats with those of the chats document.
	 */
	public void load() {
		chats.clear();
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
				.filter(chat -> !chat.getId().equals(chatSettings.get().getFallback().getChat()))
				.filter(chat -> chat.getTriggers() != null && chat.getTriggers().stream()
						.anyMatch(t -> t.getType() == TriggerType.SYMBOL && symbol.equals(((SymbolChatTrigger) t).getSymbol())))
				.sorted(Comparator.comparingInt(Chat::getPriority).reversed())
				.toList();
	}

	@Override
	public Set<InternalChat> getChats() {
		return new HashSet<>(chats.values());
	}

	@SocialisticEvent
	public void onReloaded(PluginReloadedEvent event) {
		load();
	}
}
