package me.whereareiam.socialismus.integration.protocollib.entity.model;

import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.EntityType;

public class PacketEntity {
    private final int id;
    private final EntityType entityType;
    private final PacketContainer entityPacket;
    private final PacketContainer entityMetadataPacket;

    public PacketEntity(int id, EntityType entityType, PacketContainer entityPacket, PacketContainer entityMetadataPacket) {
        this.id = id;
        this.entityType = entityType;
        this.entityPacket = entityPacket;
        this.entityMetadataPacket = entityMetadataPacket;
    }

    public int getId() {
        return id;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public PacketContainer getEntityPacket() {
        return entityPacket;
    }

    public PacketContainer getEntityMetadataPacket() {
        return entityMetadataPacket;
    }
}

