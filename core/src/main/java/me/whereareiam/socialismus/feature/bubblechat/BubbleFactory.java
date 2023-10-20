package me.whereareiam.socialismus.feature.bubblechat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.cache.Cacheable;
import me.whereareiam.socialismus.config.feature.bubblechat.BubbleChatConfig;
import me.whereareiam.socialismus.feature.bubblechat.message.BubbleMessage;
import me.whereareiam.socialismus.integration.protocollib.entity.TextDisplayPacket;
import me.whereareiam.socialismus.integration.protocollib.entity.metadata.display.TextDisplayMetadataPacket;
import me.whereareiam.socialismus.integration.protocollib.entity.model.PacketEntity;
import me.whereareiam.socialismus.util.LoggerUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@Singleton
public class BubbleFactory {
    private final LoggerUtil loggerUtil;
    private final BubbleChatConfig bubbleChatConfig;
    private final TextDisplayPacket textDisplayPacket;
    private final TextDisplayMetadataPacket textDisplayMetadataPacket;

    @Inject
    public BubbleFactory(LoggerUtil loggerUtil, BubbleChatConfig bubbleChatConfig, TextDisplayPacket textDisplayPacket,
                         TextDisplayMetadataPacket textDisplayMetadataPacket) {
        this.loggerUtil = loggerUtil;
        this.bubbleChatConfig = bubbleChatConfig;
        this.textDisplayPacket = textDisplayPacket;
        this.textDisplayMetadataPacket = textDisplayMetadataPacket;

        loggerUtil.trace("Initializing class: " + this);
    }

    @Cacheable(duration = 1)
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

        int headDistance = bubbleChatConfig.settings.headDistance;
        Component newLines = Component.text("");
        for (int i = 0; i < headDistance; i++) {
            newLines = newLines.append(Component.text("\n"));
        }

        textDisplayMeta.setMessage(bubbleMessage.message().append(newLines));

        return new PacketEntity(
                entityId,
                EntityType.TEXT_DISPLAY,
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
}

