package me.whereareiam.socialismus.module.bubblechat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.module.bubblechat.BubbleChatConfig;
import me.whereareiam.socialismus.integration.protocollib.entity.SilverfishPacket;
import me.whereareiam.socialismus.integration.protocollib.entity.TextDisplayPacket;
import me.whereareiam.socialismus.integration.protocollib.entity.metadata.MobMetadataPacket;
import me.whereareiam.socialismus.integration.protocollib.entity.metadata.display.TextDisplayMetadataPacket;
import me.whereareiam.socialismus.integration.protocollib.entity.model.PacketEntity;
import me.whereareiam.socialismus.module.bubblechat.message.BubbleMessage;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.entity.Player;

@Singleton
public class BubbleFactory {
    private final LoggerUtil loggerUtil;
    private final BubbleChatConfig bubbleChatConfig;

    private final TextDisplayPacket textDisplayPacket;
    private final TextDisplayMetadataPacket textDisplayMetadataPacket;

    private final SilverfishPacket silverfishPacket;
    private final MobMetadataPacket mobMetadataPacket;

    @Inject
    public BubbleFactory(LoggerUtil loggerUtil, BubbleChatConfig bubbleChatConfig,
                         TextDisplayPacket textDisplayPacket,
                         TextDisplayMetadataPacket textDisplayMetadataPacket,
                         SilverfishPacket silverfishPacket,
                         MobMetadataPacket mobMetadataPacket) {
        this.loggerUtil = loggerUtil;
        this.bubbleChatConfig = bubbleChatConfig;

        this.textDisplayPacket = textDisplayPacket;
        this.textDisplayMetadataPacket = textDisplayMetadataPacket;

        this.silverfishPacket = silverfishPacket;
        this.mobMetadataPacket = mobMetadataPacket;

        loggerUtil.trace("Initializing class: " + this);
    }

    public PacketEntity createBubble(BubbleMessage bubbleMessage, Player player, int entityId) {
        loggerUtil.debug("Creating bubble for " + player.getName());

        TextDisplayMetadataPacket textDisplayMeta = textDisplayMetadataPacket;
        textDisplayMeta.setAlignmentType(bubbleChatConfig.settings.alignmentType);
        textDisplayMeta.setBackground(bubbleChatConfig.format.backgroundColor, bubbleChatConfig.format.backgroundOpacity);
        textDisplayMeta.setDisplayType(bubbleChatConfig.settings.displayType);
        textDisplayMeta.setScale(bubbleChatConfig.settings.displayScale);
        textDisplayMeta.setHasShadow(bubbleChatConfig.format.textShadow);
        textDisplayMeta.setCanSeeThrough(bubbleChatConfig.settings.seeThrough);
        textDisplayMeta.setLineWidth(bubbleChatConfig.settings.lineWidth);

        textDisplayMeta.setMessage(bubbleMessage.message());

        return new PacketEntity(
                entityId,
                textDisplayPacket.createEntityPacket(
                        entityId,
                        player.getLocation()
                ),
                textDisplayMetadataPacket.createMetadataPacket(
                        entityId,
                        textDisplayMeta.getMetadata()
                )
        );
    }

    public PacketEntity createBubbleDistance(Player player, int entityId) {
        loggerUtil.debug("Creating bubble distance entity for " + player.getName());

        MobMetadataPacket mobMetadata = mobMetadataPacket;
        mobMetadata.setHasAI(false);
        mobMetadata.setVisibility(true);

        return new PacketEntity(
                entityId,
                silverfishPacket.createEntityPacket(
                        entityId,
                        player.getLocation()
                ),
                mobMetadataPacket.createMetadataPacket(
                        entityId,
                        mobMetadata.getMetadata()
                )
        );
    }
}

