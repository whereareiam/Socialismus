package me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.bubbler.api.VersionResolver;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.EntityPacket;
import me.whereareiam.socialismus.module.bubbler.api.type.DisplayType;
import me.whereareiam.socialismus.type.Version;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Base packet for spawning display entities (1.19.4+).
 * Display entities provide high-quality text and model rendering.
 */
@Getter
@SuperBuilder(toBuilder = true)
public class DisplayPacket extends EntityPacket {
	@Builder.Default
	private final Vector3f translation = new Vector3f(0.0F, 0.0F, 0.0F);
	@Builder.Default
	private final Vector3f scale = new Vector3f(1.0F, 1.0F, 1.0F);
	private final DisplayType type;

	/**
	 * Adds display-specific metadata to the metadata list.
	 * Includes translation, scale, and billboard type.
	 *
	 * @param metadata the metadata list to add to
	 */
	protected void addDisplayMetadata(List<EntityData<?>> metadata) {
		super.addCommonMetadata(metadata);

		Map<Version, Integer> translationIndex = Map.of(
				Version.V_1_20_2, 11,
				Version.V_1_19_4, 10
		);
		Map<Version, Integer> scaleIndex = Map.of(
				Version.V_1_20_2, 12,
				Version.V_1_19_4, 11
		);
		Map<Version, Integer> typeIndex = Map.of(
				Version.V_1_20_2, 15,
				Version.V_1_19_4, 14
		);

		metadata.add(new EntityData<>(
				VersionResolver.resolveOrThrow(translationIndex),
				EntityDataTypes.VECTOR3F,
				translation
		));
		metadata.add(new EntityData<>(
				VersionResolver.resolveOrThrow(scaleIndex),
				EntityDataTypes.VECTOR3F,
				scale
		));
		metadata.add(new EntityData<>(
				VersionResolver.resolveOrThrow(typeIndex),
				EntityDataTypes.BYTE,
				type.getValue()
		));
	}

	@Override
	public void send(User user) {
		WrapperPlayServerSpawnEntity spawnPacket = createSpawnPacket();
		user.sendPacket(spawnPacket);
	}

	private WrapperPlayServerSpawnEntity createSpawnPacket() {
		return new WrapperPlayServerSpawnEntity(
				entityId, Optional.of(UUID.randomUUID()), EntityTypes.DISPLAY,
				position, 0.0F, 0.0F, 0.0F, 0, Optional.empty()
		);
	}
}
