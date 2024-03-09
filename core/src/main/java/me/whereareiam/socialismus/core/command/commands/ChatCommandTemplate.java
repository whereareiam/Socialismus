package me.whereareiam.socialismus.core.command.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import com.google.inject.Inject;
import me.whereareiam.socialismus.api.model.chat.Chat;
import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import me.whereareiam.socialismus.core.chat.ChatService;
import me.whereareiam.socialismus.core.chat.message.ChatMessageFactory;
import me.whereareiam.socialismus.core.command.base.CommandBase;
import me.whereareiam.socialismus.core.config.message.MessagesConfig;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import me.whereareiam.socialismus.core.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Optional;

public class ChatCommandTemplate extends CommandBase {
	private final MessageUtil messageUtil;
	private final MessagesConfig messages;
	private final ChatMessageFactory chatMessageFactory;
	private final ChatService chatService;
	private final Plugin plugin;

	private Chat chat;

	@Inject
	public ChatCommandTemplate(LoggerUtil loggerUtil, MessageUtil messageUtil, MessagesConfig messages,
	                           ChatMessageFactory chatMessageFactory, ChatService chatService, Plugin plugin) {
		this.messageUtil = messageUtil;
		this.messages = messages;
		this.chatMessageFactory = chatMessageFactory;
		this.chatService = chatService;
		this.plugin = plugin;

		loggerUtil.trace("Initializing class: " + this);
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	@CommandAlias("%command.chat")
	@CommandPermission("%permission.chat")
	public void onCommand(CommandIssuer issuer) {
		if (!issuer.isPlayer()) {
			messageUtil.sendMessage(issuer, messages.commands.onlyForPlayer);
			return;
		}

		messageUtil.sendMessage(issuer, messages.commands.wrongSyntax);
	}

	@CommandAlias("%command.chat")
	@CommandPermission("%permission.chat")
	@CommandCompletion("@nothing")
	public void onCommand(CommandIssuer issuer, String message) {
		if (!issuer.isPlayer())
			messageUtil.sendMessage(issuer, messages.commands.onlyForPlayer);

		Player player = issuer.getIssuer();
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			ChatMessage chatMessage = chatMessageFactory.createChatMessage(player, List.of(), message, Optional.of(chat.usage.command));
			chatService.distributeMessage(chatMessage);
		});
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void addReplacements() {
		commandHelper.addReplacement(chat.usage.command, "command.chat");
		commandHelper.addReplacement(chat.requirements.sender.usePermission, "permission.chat");
	}
}
