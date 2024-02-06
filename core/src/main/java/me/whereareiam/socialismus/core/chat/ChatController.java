package me.whereareiam.socialismus.core.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.api.model.Chat;
import me.whereareiam.socialismus.api.model.ChatMessageFormat;
import me.whereareiam.socialismus.api.type.ChatUseType;
import me.whereareiam.socialismus.core.command.management.CommandRegistrar;
import me.whereareiam.socialismus.core.config.chat.ChatsConfig;
import me.whereareiam.socialismus.core.config.setting.SettingsConfig;
import me.whereareiam.socialismus.core.listener.state.ChatListenerState;
import me.whereareiam.socialismus.core.util.LoggerUtil;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ChatController implements me.whereareiam.socialismus.api.module.ChatController {
	private final LoggerUtil loggerUtil;
	private final SettingsConfig settingsConfig;
	private final ChatsConfig chatsConfig;
	private final CommandRegistrar commandRegistrar;
	private final Path modulePath;

	private List<Chat> chats = new ArrayList<>();

	private boolean moduleStatus = false;

	@Inject
	public ChatController(LoggerUtil loggerUtil, SettingsConfig settingsConfig, @Named("modulePath") Path modulePath,
	                      ChatsConfig chatsConfig, CommandRegistrar commandRegistrar) {
		this.loggerUtil = loggerUtil;
		this.settingsConfig = settingsConfig;
		this.chatsConfig = chatsConfig;
		this.modulePath = modulePath;
		this.commandRegistrar = commandRegistrar;

		loggerUtil.trace("Initializing class: " + this);
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

	@Override
	public void registerChat(Chat chat) {
		loggerUtil.debug("Registering chat: " + chat.id);
		loggerUtil.trace("Putting chat: " + chat);

		loggerUtil.trace("Chat information");
		loggerUtil.trace("Chat id: " + chat.id);
		loggerUtil.trace("Chat usage: " + chat.usage.type + " " + chat.usage.symbol + " " + chat.usage.command);
		loggerUtil.trace("Chat message formats: " + chat.formats);
		loggerUtil.trace("Chat requirements: " + chat.requirements);

		chats.add(chat);
		commandRegistrar.registerChatCommand(chat);
	}

	@Override
	public void unregisterChats() {
		chats.clear();
	}

	@Override
	public Chat getChatBySymbol(String symbol) {
		for (Chat chat : chats) {
			if (chat.usage.symbol.equals(symbol) && !chat.usage.type.equals(ChatUseType.COMMAND)) {
				return chat;
			}
		}
		return null;
	}

	@Override
	public Chat getChatByCommand(String command) {
		for (Chat chat : chats) {
			if (chat.usage.command.contains(command)) {
				return chat;
			}
		}
		return null;
	}

	@Override
	public List<Chat> getChats() {
		return chats;
	}

	@Override
	public void setChats(List<Chat> chats) {
		this.chats = chats;
	}

	private void createExampleChat() {
		Chat chat = new Chat();

		chat.id = "global";
		chat.usage.command = "global";
		chat.usage.symbol = "";
		chat.usage.type = ChatUseType.SYMBOL_COMMAND;

		ChatMessageFormat messageFormat = new ChatMessageFormat();
		messageFormat.format = "<gold><bold><globalChat>G</globalChat></bold></gold> <dark_gray>| <gray><click:run_command:/tpa {playerName><playerInformation>{playerName}</playerInformation></click>: <white><messageInformation>{message}</messageInformation>";

		chat.formats.add(messageFormat);

		chat.requirements.enabled = true;
		chat.requirements.recipient.radius = -1;
		chat.requirements.recipient.seePermission = "";
		chat.requirements.recipient.seeOwnMessage = true;
		chat.requirements.recipient.worlds = new ArrayList<>();

		chat.requirements.sender.minOnline = 0;
		chat.requirements.sender.usePermission = "";
		chat.requirements.sender.worlds = new ArrayList<>();
		chat.requirements.sender.symbolCountThreshold = 0;

		chatsConfig.chats.add(chat);
		registerChat(chat);
	}

	@Override
	public void initialize() {
		ChatListenerState.setRequired(true);
		unregisterChats();
		registerChats();

		moduleStatus = true;
	}

	@Override
	public boolean isEnabled() {
		return moduleStatus == settingsConfig.modules.chats;
	}

	@Override
	public void reload() {
		loggerUtil.trace("Before reload chats: " + chats);
		unregisterChats();

		registerChats();
		loggerUtil.trace("After reload chats: " + chats);
	}
}
