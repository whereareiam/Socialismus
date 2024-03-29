package me.whereareiam.socialismus.core.integration.protocollib.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class AreaEffectCloudPacket {
	public PacketContainer createEntityPacket(int id, Location location) {
		return new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY) {{
			getModifier().writeDefaults();
			getIntegers().write(0, id);

			getEntityTypeModifier().write(0, EntityType.AREA_EFFECT_CLOUD);

			getDoubles().write(0, location.getX());
			getDoubles().write(1, location.getY());
			getDoubles().write(2, location.getZ());
			getUUIDs().write(0, UUID.randomUUID());
		}};
	}
}
