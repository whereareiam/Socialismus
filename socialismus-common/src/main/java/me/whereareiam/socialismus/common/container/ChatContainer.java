package me.whereareiam.socialismus.common.container;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.api.EventUtil;
import me.whereareiam.socialismus.api.Reloadable;
import me.whereareiam.socialismus.api.input.container.ChatContainerService;
import me.whereareiam.socialismus.api.input.event.chat.ChatAddedEvent;
import me.whereareiam.socialismus.api.input.event.chat.ChatUpdatedEvent;
import me.whereareiam.socialismus.api.input.registry.Registry;
import me.whereareiam.socialismus.api.model.chat.Chat;
import me.whereareiam.socialismus.api.model.chat.ChatSettings;
import me.whereareiam.socialismus.api.model.chat.InternalChat;
import me.whereareiam.socialismus.api.output.LoggingHelper;
import me.whereareiam.socialismus.api.type.ChatType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ChatContainer implements ChatContainerService, Reloadable {
    private final LoggingHelper loggingHelper;
    private final Provider<ChatSettings> chatSettings;
    private final Provider<List<Chat>> chatsProvider;

    private final ConcurrentHashMap<String, InternalChat> chats = new ConcurrentHashMap<>();

    @Inject
    public ChatContainer(@Named("chats") Provider<List<Chat>> chatsProvider, LoggingHelper loggingHelper, Provider<ChatSettings> chatSettings, Registry<Reloadable> registry) {
        this.chatsProvider = chatsProvider;
        this.loggingHelper = loggingHelper;
        this.chatSettings = chatSettings;

        registry.register(this);
        chatsProvider.get().forEach(this::addChat);
    }

    @Override
    public void addChat(InternalChat chat) {
        if (chat.getId() == null || chat.getId().isEmpty()) {
            loggingHelper.debug("Chat " + chat.getId() + " has no id");
            return;
        }

        if (chats.containsKey(chat.getId())) {
            loggingHelper.debug("Chat " + chat.getId() + " already exists, but was tried to be added again");
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
                .filter(chat -> !chat.getParameters().getType().equals(ChatType.CUSTOM))
                .anyMatch(chat -> (symbol.isEmpty() && chat.getParameters().getSymbol().isEmpty())
                        || chat.getParameters().getSymbol().equals(symbol)
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
                .filter(chat -> !chat.getParameters().getType().equals(ChatType.CUSTOM))
                .filter(chat -> !chat.getId().equals(chatSettings.get().getFallback().getChatId()))
                .filter(chat -> (symbol.isEmpty() && chat.getParameters().getSymbol().isEmpty())
                        || chat.getParameters().getSymbol().equals(symbol)
                )
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
