package me.whereareiam.socialismus.listener.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.ChatService;
import me.whereareiam.socialismus.chat.message.ChatMessage;
import me.whereareiam.socialismus.chat.message.ChatMessageFactory;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.integration.protocollib.entity.EntityPacketSender;
import me.whereareiam.socialismus.integration.protocollib.entity.TextDisplayPacket;
import me.whereareiam.socialismus.integration.protocollib.entity.metadata.display.TextDisplayMetadataPacketPacketPacket;
import me.whereareiam.socialismus.integration.protocollib.entity.metadata.display.type.AlignmentType;
import me.whereareiam.socialismus.integration.protocollib.entity.metadata.display.type.DisplayType;
import me.whereareiam.socialismus.integration.protocollib.entity.model.PacketEntity;
import me.whereareiam.socialismus.util.FormatterUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

@Singleton
public class ChatHandler {
    private final ChatMessageFactory chatMessageFactory;
    private final SettingsConfig settingsConfig;
    private final ChatService chatService;

    @Inject
    private EntityPacketSender entityPacketSender;
    @Inject
    private TextDisplayPacket textDisplayPacket;
    @Inject
    private TextDisplayMetadataPacketPacketPacket textDisplayMetadataPacket;
    @Inject
    private FormatterUtil formatterUtil;
    @Inject
    private Plugin plugin;

    @Inject
    public ChatHandler(ChatMessageFactory chatMessageFactory, SettingsConfig settingsConfig, ChatService chatService) {
        this.chatMessageFactory = chatMessageFactory;
        this.settingsConfig = settingsConfig;

        this.chatService = chatService;
    }

    public void handleChatEvent(Player player, String message) {
        int id = 1;
        TextDisplayMetadataPacketPacketPacket textDisplayMeta = textDisplayMetadataPacket;
        textDisplayMeta.setAlignmentType(AlignmentType.CENTER);
        textDisplayMeta.setBackground("#FFFF00", 0);
        textDisplayMeta.setDisplayType(DisplayType.VERTICAL);
        textDisplayMeta.setHasShadow(true);
        textDisplayMeta.setCanSeeThrough(false);
        textDisplayMeta.setLineWidth(100);
        textDisplayMeta.setMessage(formatterUtil.formatMessage(player, message + "\nHello1\nHello2\n \n "));
        PacketEntity textDisplay = new PacketEntity(
                id,
                EntityType.TEXT_DISPLAY,
                textDisplayPacket.createTextDisplayEntityPacket(
                        id,
                        player.getLocation()
                ),
                textDisplayMetadataPacket.createMetadataPacket(
                        id,
                        textDisplayMeta.getMetadata()
                )
        );
        entityPacketSender.sendEntityMountPacket(
                player,
                textDisplay,
                player.getEntityId()
        );

        new BukkitRunnable() {
            @Override
            public void run() {
                entityPacketSender.removeEntitiesFromPlayer(player, List.of(textDisplay));
            }
        }.runTaskLater(plugin, 100L);

        //////////////////////
        ChatMessage chatMessage = chatMessageFactory.createChatMessage(player, message);
        if (settingsConfig.features.chats && chatMessage.getChat() != null) {
            chatService.distributeMessage(chatMessage);
        }
    }
}

