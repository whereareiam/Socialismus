package me.whereareiam.socialismus.feature.bubblechat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.feature.bubblechat.message.BubbleMessage;
import me.whereareiam.socialismus.integration.protocollib.entity.EntityPacketSender;
import me.whereareiam.socialismus.integration.protocollib.entity.model.PacketEntity;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Singleton
public class BubbleChatBroadcaster {
    private final LoggerUtil loggerUtil;
    private final BubbleFactory bubbleFactory;
    private final EntityPacketSender entityPacketSender;
    private final Map<Player, Integer> playerEntityIds = new HashMap<>();
    private final Random random = new Random();

    private PacketEntity packetEntity;

    @Inject
    public BubbleChatBroadcaster(LoggerUtil loggerUtil, BubbleFactory bubbleFactory,
                                 EntityPacketSender entityPacketSender) {
        this.loggerUtil = loggerUtil;
        this.bubbleFactory = bubbleFactory;
        this.entityPacketSender = entityPacketSender;
    }

    public void broadcastBubble(BubbleMessage bubbleMessage) {
        Player player = bubbleMessage.sender();
        int entityId;
        if (!playerEntityIds.containsKey(player)) {
            System.out.println("contains");
            entityId = random.nextInt();
            playerEntityIds.put(player, entityId);
        } else {
            System.out.println("!contains");
            entityId = playerEntityIds.get(player);
        }

        packetEntity = bubbleFactory.createBubble(bubbleMessage, player, entityId);
        for (Player onlinePlayer : bubbleMessage.receivers()) {
            entityPacketSender.sendEntityMountPacket(onlinePlayer, packetEntity, player.getEntityId());
        }
    }

    public void broadcastBubbleRemove(Player player) {
        if (playerEntityIds.containsKey(player)) {
            System.out.println("removed");
            playerEntityIds.remove(player);
            entityPacketSender.removeEntitiesGlobally(List.of(packetEntity));
        } else {
            System.out.println("No bubble to remove for " + player.getName());
        }
    }
}
