package me.whereareiam.socialismus.core.module.chatmention;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.BubbleMessage;
import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import me.whereareiam.socialismus.core.util.WorldPlayerUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

@Singleton
public class ChatMentionService {
	private final ChatMentionModule chatMentionModule;

	@Inject
	public ChatMentionService(ChatMentionModule chatMentionModule) {
		this.chatMentionModule = chatMentionModule;
	}

	public ChatMessage hookChatMention(ChatMessage chatMessage) {
		if (!chatMessage.getChat().mentions.enabled)
			return chatMessage;

		Component content = chatMessage.getContent();
		Player player = chatMessage.getSender();

		Collection<? extends Player> onlinePlayers;
		if (chatMessage.getChat().mentions.mentionAll)
			onlinePlayers = Bukkit.getOnlinePlayers();
		else
			onlinePlayers = WorldPlayerUtil.getPlayersInWorld(player.getWorld());

		onlinePlayers.removeIf(p -> !content.toString().contains(p.getName()));

		Component component = formatMessage(content, player, onlinePlayers);
		chatMessage.setContent(component);

		return chatMessage;
	}

	public BubbleMessage hookChatMention(BubbleMessage bubbleMessage) {
		Player player = bubbleMessage.getSender();
		Component content = bubbleMessage.getContent();

		Collection<? extends Player> onlinePlayers = WorldPlayerUtil.getPlayersInWorld(player.getWorld());
		onlinePlayers.removeIf(p -> !content.toString().contains(p.getName()));

		Component component = formatMessage(content, player, onlinePlayers);
		bubbleMessage.setContent(component);

		return bubbleMessage;
	}

	private Component formatMessage(Component component, Player player, Collection<? extends Player> mentioned) {
		//TODO Implement formats
		//TODO Implement notifications
		//TODO Implement per player settings

		return component;
	}
}
