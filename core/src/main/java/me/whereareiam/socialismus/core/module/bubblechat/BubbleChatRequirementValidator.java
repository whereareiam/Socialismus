package me.whereareiam.socialismus.core.module.bubblechat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.chat.message.ChatMessage;
import me.whereareiam.socialismus.core.config.message.MessagesConfig;
import me.whereareiam.socialismus.core.config.module.bubblechat.BubbleChatConfig;
import me.whereareiam.socialismus.core.config.module.bubblechat.requirements.BubbleChatRecipientRequirementConfig;
import me.whereareiam.socialismus.core.config.module.bubblechat.requirements.BubbleChatSenderRequirementConfig;
import me.whereareiam.socialismus.core.util.MessageUtil;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

@Singleton
public class BubbleChatRequirementValidator {
	private final MessageUtil messageUtil;
	private final MessagesConfig messagesConfig;
	private final BubbleChatConfig bubbleChatConfig;

	@Inject
	public BubbleChatRequirementValidator(MessageUtil messageUtil, MessagesConfig messagesConfig,
										  BubbleChatConfig bubbleChatConfig) {
		this.messageUtil = messageUtil;
		this.messagesConfig = messagesConfig;
		this.bubbleChatConfig = bubbleChatConfig;
	}

	public Collection<? extends Player> validatePlayers(Player sender, Collection<? extends Player> onlinePlayers) {
		BubbleChatRecipientRequirementConfig recipientRequirements = bubbleChatConfig.requirements.recipient;
		if (! bubbleChatConfig.requirements.enabled)
			return onlinePlayers;

		if (! recipientRequirements.seeOwnBubble)
			onlinePlayers.remove(sender);

		if (recipientRequirements.seePermission != null && ! recipientRequirements.seePermission.isEmpty())
			onlinePlayers = onlinePlayers.stream().filter(player -> player.hasPermission(recipientRequirements.seePermission)).collect(Collectors.toList());

		return onlinePlayers;
	}

	public boolean validatePlayer(ChatMessage chatMessage) {
		BubbleChatSenderRequirementConfig senderRequirements = bubbleChatConfig.requirements.sender;
		if (! bubbleChatConfig.requirements.enabled)
			return true;

		Player sender = chatMessage.getSender();
		String message = PlainTextComponentSerializer.plainText().serialize(chatMessage.getContent());

		if (senderRequirements.usePermission != null && ! sender.hasPermission(senderRequirements.usePermission)) {
			messageUtil.sendMessage(sender, messagesConfig.bubblechat.noUsePermission);
			return false;
		}

		if (message.length() <= senderRequirements.symbolCountThreshold) {
			messageUtil.sendMessage(sender, messagesConfig.bubblechat.tooSmallMessage);
			return false;
		}

		if (! senderRequirements.worlds.isEmpty() && ! senderRequirements.worlds.contains(sender.getWorld().getName())) {
			messageUtil.sendMessage(sender, messagesConfig.bubblechat.forbiddenWorld);
			return false;
		}

		return true;
	}
}
