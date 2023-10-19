package me.whereareiam.socialismus.integration.protocollib.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.integration.protocollib.entity.model.PacketEntity;
import org.bukkit.entity.Player;

import java.util.List;

@Singleton
public class EntityPacketSender {
    private final me.whereareiam.socialismus.integration.protocollib.PacketSender packetSender;

    @Inject
    public EntityPacketSender(me.whereareiam.socialismus.integration.protocollib.PacketSender packetSender) {
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

    public void removeEntitiesFromPlayer(Player player, List<PacketEntity> packetEntity) {
        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, packetEntity.stream().map(PacketEntity::getId).toList());

        packetSender.sendPacket(player, destroyPacket);
    }

    public void removeEntitiesGlobally(List<PacketEntity> packetEntity) {
        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, packetEntity.stream().map(PacketEntity::getId).toList());

        packetSender.broadcastPacket(destroyPacket);
    }
}