package me.whereareiam.socialismus.feature.bubblechat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.feature.bubblechat.BubbleChatConfig;
import me.whereareiam.socialismus.feature.bubblechat.message.BubbleMessage;
import me.whereareiam.socialismus.integration.protocollib.entity.EntityPacketSender;
import me.whereareiam.socialismus.integration.protocollib.entity.model.PacketEntity;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.entity.Player;

import java.util.*;

@Singleton
public class BubbleChatBroadcaster {
    private final LoggerUtil loggerUtil;
    private final BubbleFactory bubbleFactory;
    private final EntityPacketSender entityPacketSender;
    private final Random random = new Random();

    private final Map<Player, Integer> playerEntityIds = new HashMap<>();
    private final Map<Player, List<PacketEntity>> playerEntities = new HashMap<>();

    private final BubbleChatConfig bubbleChatConfig;
    private PacketEntity packetEntity;

    @Inject
    public BubbleChatBroadcaster(LoggerUtil loggerUtil, BubbleFactory bubbleFactory,
                                 EntityPacketSender entityPacketSender, BubbleChatConfig bubbleChatConfig) {
        this.loggerUtil = loggerUtil;
        this.bubbleFactory = bubbleFactory;
        this.entityPacketSender = entityPacketSender;
        this.bubbleChatConfig = bubbleChatConfig;
    }

    public void broadcastBubble(BubbleMessage bubbleMessage) {
        loggerUtil.debug("Broadcasting bubble");
        Player player = bubbleMessage.sender();
        int entityId;
        if (!playerEntityIds.containsKey(player)) {
            entityId = random.nextInt();
            playerEntityIds.put(player, entityId);
        } else {
            entityId = playerEntityIds.get(player);
        }

        List<PacketEntity> entities = new ArrayList<>();
        int previousEntityId = player.getEntityId();
        for (int i = 0; i < bubbleChatConfig.settings.headDistance; i++) {
            PacketEntity invisibleEntity = bubbleFactory.createBubbleDistance(player, random.nextInt());
            entityPacketSender.sendEntityMountPacket(player, invisibleEntity, previousEntityId);
            entities.add(invisibleEntity);
            previousEntityId = invisibleEntity.getId();
        }
        playerEntities.put(player, entities);

        packetEntity = bubbleFactory.createBubble(bubbleMessage, player, entityId);
        for (Player onlinePlayer : bubbleMessage.receivers()) {
            entityPacketSender.sendEntityMountPacket(onlinePlayer, packetEntity, previousEntityId);
        }
    }

    public void broadcastBubbleRemove(Player player) {
        if (playerEntities.containsKey(player)) {
            entityPacketSender.removeEntitiesGlobally(List.of(packetEntity));
            List<PacketEntity> entities = playerEntities.get(player);
            for (PacketEntity entity : entities) {
                entityPacketSender.removeEntitiesGlobally(List.of(entity));
            }
            playerEntities.remove(player);
        } else {
            loggerUtil.severe("No bubble to remove for " + player.getName());
        }
    }
}
