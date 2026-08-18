package me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.bubbler.api.VersionResolver;
import me.whereareiam.socialismus.type.Version;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Packet for spawning an area effect cloud entity.
 * Used as an invisible carrier entity for mounting bubble displays
 * on older Minecraft versions.
 */
@Getter
@SuperBuilder(toBuilder = true)
public class AreaEffectCloudPacket extends EntityPacket {
	private final float radius;

	/** Version-specific metadata index for the radius field */
	private static final Map<Version, Integer> RADIUS_INDEX = Map.of(
			Version.V_1_16, 8
	);

	@Override
	public void send(User user) {
		WrapperPlayServerSpawnEntity spawnPacket = createSpawnPacket();
		WrapperPlayServerEntityMetadata metadataPacket = createMetadataPacket();

		user.sendPacket(spawnPacket);
		user.sendPacket(metadataPacket);
	}

	private WrapperPlayServerSpawnEntity createSpawnPacket() {
		return new WrapperPlayServerSpawnEntity(
				entityId, Optional.of(UUID.randomUUID()), EntityTypes.AREA_EFFECT_CLOUD,
				position, 0.0F, 0.0F, 0.0F, 0, Optional.empty()
		);
	}

	private WrapperPlayServerEntityMetadata createMetadataPacket() {
		List<EntityData<?>> metadata = new ArrayList<>();
		addCommonMetadata(metadata);

		metadata.add(new EntityData<>(
				VersionResolver.resolveOrThrow(RADIUS_INDEX),
				EntityDataTypes.FLOAT,
				radius
		));

		return new WrapperPlayServerEntityMetadata(entityId, metadata);
	}
}
