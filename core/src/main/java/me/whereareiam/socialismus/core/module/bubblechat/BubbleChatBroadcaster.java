package me.whereareiam.socialismus.core.module.bubblechat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.event.bubblechat.AfterBubbleSendMessageEvent;
import me.whereareiam.socialismus.api.event.bubblechat.OnBubbleSendMessageEvent;
import me.whereareiam.socialismus.api.model.BubbleMessage;
import me.whereareiam.socialismus.core.config.module.bubblechat.BubbleChatConfig;
import me.whereareiam.socialismus.core.integration.protocollib.entity.EntityPacketSender;
import me.whereareiam.socialismus.core.integration.protocollib.entity.model.PacketEntity;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Singleton
public class BubbleChatBroadcaster {
	private final LoggerUtil loggerUtil;
	private final BubbleFactory bubbleFactory;
	private final EntityPacketSender entityPacketSender;
	private final Random random = new Random();

	private final Map<Player, List<PacketEntity>> playerEntities = new HashMap<>();

	private final BubbleChatConfig bubbleChatConfig;

	@Inject
	public BubbleChatBroadcaster(LoggerUtil loggerUtil, BubbleFactory bubbleFactory,
	                             EntityPacketSender entityPacketSender, BubbleChatConfig bubbleChatConfig) {
		this.loggerUtil = loggerUtil;
		this.bubbleFactory = bubbleFactory;
		this.entityPacketSender = entityPacketSender;
		this.bubbleChatConfig = bubbleChatConfig;
	}

	public void broadcastBubble(BubbleMessage bubbleMessage) {
		OnBubbleSendMessageEvent event = new OnBubbleSendMessageEvent(bubbleMessage);
		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return;
		}

		Player player = event.getBubbleMessage().getSender();
		if (bubbleChatConfig.settings.sound.senderSound != null)
			player.playSound(player.getLocation(), bubbleChatConfig.settings.sound.senderSound, bubbleChatConfig.settings.sound.senderSoundVolume, bubbleChatConfig.settings.sound.senderSoundPitch);

		bubbleMessage = event.getBubbleMessage();

		List<PacketEntity> entities = new ArrayList<>();
		entities.add(bubbleFactory.createBubble(bubbleMessage, player, random.nextInt()));
		for (int i = 0; i < bubbleChatConfig.settings.headDistance; i++) {
			PacketEntity invisibleEntity = bubbleFactory.createBubbleDistance(player, random.nextInt());
			entities.add(invisibleEntity);
		}
		playerEntities.put(player, entities);

		loggerUtil.debug("Broadcasting bubble");
		for (Player onlinePlayer : bubbleMessage.getRecipients()) {
			int previousEntityId = player.getEntityId();
			for (int i = 1; i < entities.size(); i++) {
				PacketEntity entity = entities.get(i);
				entityPacketSender.sendEntityMountPacket(onlinePlayer, entity, previousEntityId);
				previousEntityId = entity.getId();
			}

			if (bubbleChatConfig.settings.sound.receiverSound != null)
				onlinePlayer.playSound(onlinePlayer.getLocation(), bubbleChatConfig.settings.sound.receiverSound, bubbleChatConfig.settings.sound.receiverSoundVolume, bubbleChatConfig.settings.sound.receiverSoundPitch);

			entityPacketSender.sendEntityMountPacket(onlinePlayer, entities.get(0), previousEntityId);
		}

		AfterBubbleSendMessageEvent afterEvent = new AfterBubbleSendMessageEvent(bubbleMessage);
		Bukkit.getPluginManager().callEvent(afterEvent);
	}

	public void broadcastBubbleRemove(Player player) {
		if (playerEntities.containsKey(player)) {
			List<PacketEntity> entities = playerEntities.get(player).stream().toList();
			entityPacketSender.removeEntitysGlobally(entities);
			playerEntities.remove(player);
		} else {
			loggerUtil.severe("No bubble to remove for " + player.getName());
		}
	}
}
