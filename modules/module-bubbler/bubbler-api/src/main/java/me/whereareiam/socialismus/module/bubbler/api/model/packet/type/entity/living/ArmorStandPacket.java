package me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.living;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.bubbler.api.VersionResolver;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.EntityPacket;
import me.whereareiam.socialismus.type.Version;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Packet for spawning an armor stand entity with a custom name.
 * Used for displaying bubble text on older Minecraft versions
 * that don't support text display entities.
 */
@Getter
@SuperBuilder(toBuilder = true)
public class ArmorStandPacket extends EntityPacket {
	@Builder.Default
	private final boolean small = true;
	@Builder.Default
	private final boolean invisible = true;
	@Builder.Default
	private final boolean marker = true;
	private final Component customName;
	@Builder.Default
	private final boolean customNameVisible = true;

	/** Version-specific metadata index for entity flags */
	private static final Map<Version, Integer> ENTITY_FLAGS_INDEX = Map.of(
			Version.V_1_16, 0
	);

	/** Version-specific metadata index for custom name */
	private static final Map<Version, Integer> CUSTOM_NAME_INDEX = Map.of(
			Version.V_1_16, 2
	);

	/** Version-specific metadata index for custom name visibility */
	private static final Map<Version, Integer> CUSTOM_NAME_VISIBLE_INDEX = Map.of(
			Version.V_1_16, 3
	);

	/** Version-specific metadata index for armor stand flags */
	private static final Map<Version, Integer> ARMOR_STAND_FLAGS_INDEX = Map.of(
			Version.V_1_16, 15
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
				entityId, Optional.of(UUID.randomUUID()), EntityTypes.ARMOR_STAND,
				position, 0.0F, 0.0F, 0.0F, 0, Optional.empty()
		);
	}

	private WrapperPlayServerEntityMetadata createMetadataPacket() {
		List<EntityData<?>> metadata = new ArrayList<>();
		addCommonMetadata(metadata);

		byte entityFlags = 0;
		if (invisible) entityFlags |= 0x20;
		metadata.add(new EntityData<>(
				VersionResolver.resolveOrThrow(ENTITY_FLAGS_INDEX),
				EntityDataTypes.BYTE,
				entityFlags
		));

		if (customName != null) {
			metadata.add(new EntityData<>(
					VersionResolver.resolveOrThrow(CUSTOM_NAME_INDEX),
					EntityDataTypes.OPTIONAL_ADV_COMPONENT,
					Optional.of(customName)
			));
		}

		metadata.add(new EntityData<>(
				VersionResolver.resolveOrThrow(CUSTOM_NAME_VISIBLE_INDEX),
				EntityDataTypes.BOOLEAN,
				customNameVisible
		));

		byte armorStandFlags = 0;
		if (small) armorStandFlags |= 0x01;
		if (marker) armorStandFlags |= 0x10;
		metadata.add(new EntityData<>(
				VersionResolver.resolveOrThrow(ARMOR_STAND_FLAGS_INDEX),
				EntityDataTypes.BYTE,
				armorStandFlags
		));

		return new WrapperPlayServerEntityMetadata(entityId, metadata);
	}
}
