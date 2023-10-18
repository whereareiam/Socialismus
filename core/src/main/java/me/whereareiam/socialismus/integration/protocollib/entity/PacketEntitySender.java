package me.whereareiam.socialismus.integration.protocollib.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.integration.protocollib.entity.model.PacketEntity;
import org.bukkit.entity.Player;

@Singleton
public class PacketEntitySender {
    private final me.whereareiam.socialismus.integration.protocollib.PacketSender packetSender;

    @Inject
    public PacketEntitySender(me.whereareiam.socialismus.integration.protocollib.PacketSender packetSender) {
        this.packetSender = packetSender;
    }

    public void sendEntityPacket(Player player, PacketEntity packetEntity) {
        packetSender.sendPacket(player, packetEntity.getEntityPacket());
        packetSender.sendPacket(player, packetEntity.getEntityMetadataPacket());
    }

    public void broadcastEntityPacket(PacketEntity packetEntity) {
        packetSender.broadcastPacket(packetEntity.getEntityPacket());
        packetSender.broadcastPacket(packetEntity.getEntityMetadataPacket());
    }

    public void sendEntityMountPacket(Player player, PacketEntity packetEntity, int mountEntityId) {
        PacketContainer mountPacket = new PacketContainer(PacketType.Play.Server.MOUNT);
        mountPacket.getIntegers().write(0, mountEntityId);
        mountPacket.getIntegerArrays().write(0, new int[]{packetEntity.getId()});

        sendEntityPacket(player, packetEntity);
        packetSender.sendPacket(player, mountPacket);
    }

    public void broadcastEntityMountPacket(PacketEntity packetEntity, int mountEntityId) {
        PacketContainer mountPacket = new PacketContainer(PacketType.Play.Server.MOUNT);
        mountPacket.getIntegers().write(0, mountEntityId);
        mountPacket.getIntegerArrays().write(0, new int[]{packetEntity.getId()});

        broadcastEntityPacket(packetEntity);
        packetSender.broadcastPacket(mountPacket);
    }

    public void removeEntityFromPlayer(Player player, PacketEntity packetEntity) {
        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntegerArrays().write(0, new int[]{packetEntity.getId()});

        packetSender.sendPacket(player, destroyPacket);
    }

    public void removeEntityGlobally(PacketEntity packetEntity) {
        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntegerArrays().write(0, new int[]{packetEntity.getId()});

        packetSender.broadcastPacket(destroyPacket);
    }
}