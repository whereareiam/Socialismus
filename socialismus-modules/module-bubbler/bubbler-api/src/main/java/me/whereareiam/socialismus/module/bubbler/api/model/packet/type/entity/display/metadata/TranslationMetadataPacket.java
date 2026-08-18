package me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.metadata;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.Packet;
import me.whereareiam.socialismus.module.bubbler.api.VersionResolver;
import me.whereareiam.socialismus.type.Version;

import java.util.List;
import java.util.Map;

/**
 * Metadata packet for updating a display entity's translation offset.
 * Used for animations that move the display relative to its position.
 */
@SuperBuilder
public final class TranslationMetadataPacket implements Packet {
	private final int entityId;
	private final Vector3f translation;

	@Override
	public void send(User user) {
		user.sendPacket(new WrapperPlayServerEntityMetadata(entityId, createMetadata()));
	}

	private List<EntityData<?>> createMetadata() {
		Map<Version, Integer> translationIndex = Map.of(
				Version.V_1_20_2, 11,
				Version.V_1_19_4, 11
		);

		Integer index = VersionResolver.resolve(translationIndex, null);
		if (index == null) return List.of();

		return List.of(
				new EntityData<>(index, EntityDataTypes.VECTOR3F, translation)
		);
	}
}
