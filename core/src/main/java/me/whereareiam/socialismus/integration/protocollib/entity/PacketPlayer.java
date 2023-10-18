package me.whereareiam.socialismus.integration.protocollib.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.inject.Singleton;

import java.util.Collections;
import java.util.EnumSet;
import java.util.UUID;

@Singleton
public class PacketPlayer {
    public PacketContainer createPacketPlayerInfo(String username) {
        return new PacketContainer(PacketType.Play.Server.PLAYER_INFO) {{
            getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
            getPlayerInfoDataLists().write(1, Collections.singletonList(new PlayerInfoData(
                    new WrappedGameProfile(UUID.randomUUID(), username),
                    1,
                    EnumWrappers.NativeGameMode.SURVIVAL,
                    WrappedChatComponent.fromText(username)
            )));
        }};
    }
}
