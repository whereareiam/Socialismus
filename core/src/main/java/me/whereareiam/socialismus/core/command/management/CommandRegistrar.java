package me.whereareiam.socialismus.core.command.management;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.chat.Chat;
import me.whereareiam.socialismus.api.type.ChatUseType;
import me.whereareiam.socialismus.core.command.commands.*;

@Singleton
public class CommandRegistrar {
	private final Injector injector;
	private final CommandManager commandManager;

	@Inject
	public CommandRegistrar(Injector injector, CommandManager commandManager) {
		this.injector = injector;
		this.commandManager = commandManager;
	}

	public void registerCommands() {
		commandManager.registerCommand(injector.getInstance(MainCommand.class));
		commandManager.registerCommand(injector.getInstance(ReloadCommand.class));
		commandManager.registerCommand(injector.getInstance(PrivateMessageCommand.class));
		commandManager.registerCommand(injector.getInstance(AnnounceCommand.class));
		commandManager.registerCommand(injector.getInstance(BubbleCommand.class));
	}

	public void registerChatCommand(Chat chat) {
		if (chat.usage.type.equals(ChatUseType.SYMBOL_COMMAND) || chat.usage.type.equals(ChatUseType.COMMAND)) {
			ChatCommandTemplate commandTemplate = injector.getInstance(ChatCommandTemplate.class);
			commandTemplate.setChat(chat);
			commandManager.registerCommand(commandTemplate);
		}
	}
}
