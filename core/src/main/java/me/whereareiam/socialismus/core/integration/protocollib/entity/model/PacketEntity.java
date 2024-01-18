package me.whereareiam.socialismus.core.integration.protocollib.entity.model;

import com.comphenix.protocol.events.PacketContainer;

public class PacketEntity {
	private final int id;
	private final PacketContainer entityPacket;
	private final PacketContainer entityMetadataPacket;

	public PacketEntity(int id, PacketContainer entityPacket, PacketContainer entityMetadataPacket) {
		this.id = id;
		this.entityPacket = entityPacket;
		this.entityMetadataPacket = entityMetadataPacket;
	}

	public int getId() {
		return id;
	}

	public PacketContainer getEntityPacket() {
		return entityPacket;
	}

	public PacketContainer getEntityMetadataPacket() {
		return entityMetadataPacket;
	}
}

