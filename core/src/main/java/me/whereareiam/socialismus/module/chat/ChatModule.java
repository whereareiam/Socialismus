package me.whereareiam.socialismus.module.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.chat.ChatUseType;
import me.whereareiam.socialismus.command.management.CommandRegistrar;
import me.whereareiam.socialismus.config.module.chat.ChatsConfig;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.listener.state.ChatListenerState;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.module.Module;
import me.whereareiam.socialismus.util.LoggerUtil;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ChatModule implements Module {
    private final LoggerUtil loggerUtil;
    private final SettingsConfig settingsConfig;
    private final ChatsConfig chatsConfig;
    private final CommandRegistrar commandRegistrar;
    private final Path modulePath;
    private final Map<String, Chat> chats = new HashMap<>();
    private boolean moduleStatus = false;

    @Inject
    public ChatModule(LoggerUtil loggerUtil, SettingsConfig settingsConfig, @Named("modulePath") Path modulePath, ChatsConfig chatsConfig,
                      CommandRegistrar commandRegistrar) {
        this.loggerUtil = loggerUtil;
        this.settingsConfig = settingsConfig;
        this.chatsConfig = chatsConfig;
        this.modulePath = modulePath;
        this.commandRegistrar = commandRegistrar;

        loggerUtil.trace("Initializing class: " + this);
    }

    public void registerChat(Chat chat) {
        loggerUtil.debug("Registering chat: " + chat.id);
        loggerUtil.trace("Putting chat: " + chat);

        loggerUtil.trace("Chat information");
        loggerUtil.trace("Chat id: " + chat.id);
        loggerUtil.trace("Chat usage: " + chat.usage.type + " " + chat.usage.symbol + " " + chat.usage.command);
        loggerUtil.trace("Chat message format: " + chat.messageFormat);
        loggerUtil.trace("Chat hover format: " + chat.hoverFormat.stream().toString());
        loggerUtil.trace("Chat requirements: " + chat.requirements);

        chats.put(chat.id, chat);
        commandRegistrar.registerChatCommand(chat);
    }

    private void registerChats() {
        loggerUtil.debug("Reloading chats.yml");
        chatsConfig.reload(modulePath.resolve("chats.yml"));

        if (chatsConfig.chats.isEmpty()) {
            loggerUtil.debug("Creating an example chat, because chats.yml is empty");
            createExampleChat();
            chatsConfig.save(modulePath.resolve("chats.yml"));
        } else {
            for (Chat chat : chatsConfig.chats) {
                registerChat(chat);
            }
        }
    }

    public void cleanChats() {
        chats.clear();
        loggerUtil.trace("All chats have been cleaned");
    }

    public Chat getChatBySymbol(String symbol) {
        for (Chat chat : chats.values()) {
            if (chat.usage.symbol.equals(symbol) && !chat.usage.type.equals(ChatUseType.COMMAND)) {
                return chat;
            }
        }
        return null;
    }

    public Chat getChatByCommand(String command) {
        for (Chat chat : chats.values()) {
            if (chat.usage.command.contains(command)) {
                return chat;
            }
        }
        return null;
    }

    public int getChatCount() {
        return chats.size();
    }

    @Override
    public void initialize() {
        ChatListenerState.setRequired(true);
        registerChats();

        moduleStatus = true;
    }

    @Override
    public boolean isEnabled() {
        return moduleStatus == settingsConfig.modules.chats;
    }

    @Override
    public void reload() {
        loggerUtil.trace("Before reload chats: " + chats.values());
        cleanChats();

        registerChats();
        loggerUtil.trace("After reload chats: " + chats.values());
    }

    private void createExampleChat() {
        Chat chat = new Chat();

        chat.id = "example";
        chat.usage.command = "example";
        chat.usage.symbol = "";
        chat.usage.type = ChatUseType.SYMBOL_COMMAND;

        chat.messageFormat = "<red>{playerName}: <white>{message}";
        chat.hoverFormat.add("<blue>Some text1");
        chat.hoverFormat.add("<blue>Some text2");

        chat.requirements.enabled = true;
        chat.requirements.recipient.radius = -1;
        chat.requirements.recipient.seePermission = "";
        chat.requirements.recipient.seeOwnMessage = true;
        chat.requirements.recipient.worlds = List.of("world", "world_nether", "world_the_end");

        chat.requirements.sender.minOnline = 0;
        chat.requirements.sender.usePermission = "";
        chat.requirements.sender.worlds = List.of("world", "world_nether", "world_the_end");
        chat.requirements.sender.symbolCountThreshold = 0;

        chatsConfig.chats.add(chat);
        registerChat(chat);
    }
}
