package me.whereareiam.socialismus.core.module.bubblechat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.BubbleMessage;
import me.whereareiam.socialismus.core.config.module.bubblechat.BubbleChatConfig;
import me.whereareiam.socialismus.core.integration.protocollib.entity.AreaEffectCloudPacket;
import me.whereareiam.socialismus.core.integration.protocollib.entity.TextDisplayPacket;
import me.whereareiam.socialismus.core.integration.protocollib.entity.metadata.AreaEffectCloudMetadataPacket;
import me.whereareiam.socialismus.core.integration.protocollib.entity.metadata.display.TextDisplayMetadataPacket;
import me.whereareiam.socialismus.core.integration.protocollib.entity.model.PacketEntity;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import org.bukkit.entity.Player;

@Singleton
public class BubbleFactory {
	private final LoggerUtil loggerUtil;
	private final BubbleChatConfig bubbleChatConfig;

	private final TextDisplayPacket textDisplayPacket;
	private final TextDisplayMetadataPacket textDisplayMetadataPacket;

	private final AreaEffectCloudPacket areaEffectCloudPacket;
	private final AreaEffectCloudMetadataPacket areaEffectCloudMetadataPacket;

	@Inject
	public BubbleFactory(LoggerUtil loggerUtil, BubbleChatConfig bubbleChatConfig,
						 TextDisplayPacket textDisplayPacket,
						 TextDisplayMetadataPacket textDisplayMetadataPacket,
						 AreaEffectCloudPacket areaEffectCloudPacket,
						 AreaEffectCloudMetadataPacket areaEffectCloudMetadataPacket) {
		this.loggerUtil = loggerUtil;
		this.bubbleChatConfig = bubbleChatConfig;

		this.textDisplayPacket = textDisplayPacket;
		this.textDisplayMetadataPacket = textDisplayMetadataPacket;

		this.areaEffectCloudPacket = areaEffectCloudPacket;
		this.areaEffectCloudMetadataPacket = areaEffectCloudMetadataPacket;

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

		textDisplayMeta.setMessage(bubbleMessage.getContent());

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

		AreaEffectCloudMetadataPacket areaEffectCloudMetadata = areaEffectCloudMetadataPacket;
		areaEffectCloudMetadata.setRadius(0.0f);

		return new PacketEntity(
				entityId,
				areaEffectCloudPacket.createEntityPacket(
						entityId,
						player.getLocation()
				),
				areaEffectCloudMetadata.createMetadataPacket(
						entityId,
						areaEffectCloudMetadata.getMetadata()
				)
		);
	}
}

