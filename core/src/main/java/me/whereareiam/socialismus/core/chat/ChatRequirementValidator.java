package me.whereareiam.socialismus.core.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import me.whereareiam.socialismus.api.model.chat.ChatRecipientRequirements;
import me.whereareiam.socialismus.api.model.chat.ChatSenderRequirements;
import me.whereareiam.socialismus.core.config.message.MessagesConfig;
import me.whereareiam.socialismus.core.util.DistanceUtil;
import me.whereareiam.socialismus.core.util.MessageUtil;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

@Singleton
public class ChatRequirementValidator {
	private final MessageUtil messageUtil;
	private final MessagesConfig messagesConfig;

	@Inject
	public ChatRequirementValidator(MessageUtil messageUtil, MessagesConfig messagesConfig) {
		this.messageUtil = messageUtil;
		this.messagesConfig = messagesConfig;
	}

	public Collection<? extends Player> validatePlayers(ChatMessage chatMessage) {
		Collection<? extends Player> recipients = chatMessage.getRecipients();
		ChatRecipientRequirements recipientRequirements = chatMessage.getChat().requirements.recipient;
		if (!chatMessage.getChat().requirements.enabled)
			return recipients;

		Player sender = chatMessage.getSender();
		if (!recipientRequirements.seeOwnMessage)
			recipients.remove(sender);

		if (recipientRequirements.seePermission != null && !recipientRequirements.seePermission.isEmpty())
			recipients = recipients
					.stream()
					.filter(player -> player.hasPermission(recipientRequirements.seePermission))
					.collect(Collectors.toList());

		if (!recipientRequirements.worlds.isEmpty())
			recipients = recipients
					.stream()
					.filter(player -> recipientRequirements.worlds.contains(player.getWorld().getName()))
					.collect(Collectors.toList());

		if (recipientRequirements.radius >= 0)
			recipients = recipients
					.stream()
					.filter(player -> {
						double distance = DistanceUtil.between(player, sender);
						return distance != -1 && distance <= recipientRequirements.radius;
					})
					.collect(Collectors.toList());

		return recipients;
	}

	public boolean validatePlayer(ChatMessage chatMessage) {
		ChatSenderRequirements senderRequirements = chatMessage.getChat().requirements.sender;
		if (!chatMessage.getChat().requirements.enabled)
			return true;

		Player sender = chatMessage.getSender();
		String message = PlainTextComponentSerializer.plainText().serialize(chatMessage.getContent());

		if (Bukkit.getOnlinePlayers().size() <= senderRequirements.minOnline) {
			messageUtil.sendMessage(sender, messagesConfig.chat.insufficientPlayers
					.replace("{minOnline}", String.valueOf(senderRequirements.minOnline)));
			return false;
		}

		if (senderRequirements.usePermission != null && !senderRequirements.usePermission.isEmpty() && !sender.hasPermission(senderRequirements.usePermission)) {
			messageUtil.sendMessage(sender, messagesConfig.chat.noUsePermission);
			return false;
		}

		if (message.length() <= senderRequirements.symbolCountThreshold) {
			messageUtil.sendMessage(sender, messagesConfig.chat.tooSmallMessage);
			return false;
		}

		if (!senderRequirements.worlds.isEmpty() && !senderRequirements.worlds.contains(sender.getWorld().getName())) {
			messageUtil.sendMessage(sender, messagesConfig.chat.forbiddenWorld);
			return false;
		}

		return true;
	}
}
