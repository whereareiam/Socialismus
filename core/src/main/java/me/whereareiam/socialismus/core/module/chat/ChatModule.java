package me.whereareiam.socialismus.core.module.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.api.model.chat.Chat;
import me.whereareiam.socialismus.api.model.chat.ChatMessageFormat;
import me.whereareiam.socialismus.api.type.ChatUseType;
import me.whereareiam.socialismus.core.command.management.CommandRegistrar;
import me.whereareiam.socialismus.core.config.module.chat.ChatsConfig;
import me.whereareiam.socialismus.core.config.setting.SettingsConfig;
import me.whereareiam.socialismus.core.listener.state.ChatListenerState;
import me.whereareiam.socialismus.core.util.LoggerUtil;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ChatModule implements me.whereareiam.socialismus.api.module.ChatModule {
	private final LoggerUtil loggerUtil;
	private final SettingsConfig settingsConfig;
	private final ChatsConfig chatsConfig;
	private final CommandRegistrar commandRegistrar;
	private final Path modulePath;

	private List<Chat> chats = new ArrayList<>();

	private boolean moduleStatus = false;

	@Inject
	public ChatModule(LoggerUtil loggerUtil, SettingsConfig settingsConfig, @Named("modulePath") Path modulePath,
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
		loggerUtil.trace("Chat hover format: " + chat.hoverFormat.stream().toString());
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
		messageFormat.format = "<gold><bold><insert:/global message>G</insert><reset> <dark_gray>| <gray><click:run_command:/tpa {playerName>%luckperms_prefix%{playerName}</click>: <white>{message}";

		chat.formats.add(messageFormat);
		chat.hoverFormat.add(" ");
		chat.hoverFormat.add("<dark_gray> Information");
		chat.hoverFormat.add("<gray>  Message was sent at: <gold>%player_world_time_24%");
		chat.hoverFormat.add(" ");
		chat.hoverFormat.add("<gray>  World: <gold>%player_world_name%");
		chat.hoverFormat.add("<gray>  XYZ: <gold>%player_location_x%, %player_location_y%, %player_location_z%");
		chat.hoverFormat.add(" ");

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
