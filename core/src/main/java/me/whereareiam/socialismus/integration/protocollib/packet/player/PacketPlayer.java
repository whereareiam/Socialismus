package me.whereareiam.socialismus.integration.protocollib.packet.player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.EnumSet;
import java.util.UUID;

@Singleton
public class PacketPlayer {
    private final ProtocolManager protocolManager;

    @Inject
    public PacketPlayer(ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
    }

    public void sendPacketPlayer(Player recipient, String username) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
        packet.getPlayerInfoDataLists().write(1, Collections.singletonList(new PlayerInfoData(
                new WrappedGameProfile(UUID.randomUUID(), username),
                1,
                EnumWrappers.NativeGameMode.SURVIVAL,
                WrappedChatComponent.fromText(username)
        )));

        protocolManager.sendServerPacket(recipient, packet);
    }
}
