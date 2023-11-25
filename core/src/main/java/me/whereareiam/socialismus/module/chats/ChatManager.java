package me.whereareiam.socialismus.module.chats;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.cache.Cacheable;
import me.whereareiam.socialismus.chat.ChatUseType;
import me.whereareiam.socialismus.command.management.CommandRegistrar;
import me.whereareiam.socialismus.config.module.chat.ChatsConfig;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.module.IModule;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class ChatManager implements IModule {
    private final LoggerUtil loggerUtil;
    private final ChatsConfig chatsConfig;
    private final CommandRegistrar commandRegistrar;
    private final Path modulePath;

    private final Map<String, Chat> chats = new HashMap<>();

    @Inject
    public ChatManager(LoggerUtil loggerUtil, Plugin plugin, ChatsConfig chatsConfig,
                       CommandRegistrar commandRegistrar) {
        this.loggerUtil = loggerUtil;
        this.chatsConfig = chatsConfig;
        this.modulePath = plugin.getDataFolder().toPath().resolve("modules");
        this.commandRegistrar = commandRegistrar;

        loggerUtil.trace("Initializing class: " + this);
    }

    public void registerChat(Chat chat) {
        loggerUtil.trace("Registering chat: " + chat.id);

        chats.put(chat.id, chat);
        commandRegistrar.registerChatCommand(chat);
    }

    public void registerChats() {
        chatsConfig.reload(modulePath.resolve("chats.yml"));

        for (Chat chat : chatsConfig.chats) {
            registerChat(chat);
        }
    }

    public void cleanChats() {
        chats.clear();
        loggerUtil.trace("All chats have been cleaned");
    }

    @Cacheable(duration = 5)
    public Chat getChatBySymbol(String symbol) {
        for (Chat chat : chats.values()) {
            if (chat.usage.symbol.equals(symbol) && !chat.usage.type.equals(ChatUseType.COMMAND)) {
                return chat;
            }
        }
        return null;
    }

    @Cacheable(duration = 5)
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
    public boolean requiresChatListener() {
        return true;
    }

    @Override
    public boolean requiresJoinListener() {
        return false;
    }
}
