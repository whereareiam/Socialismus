package me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.metadata;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.Packet;
import me.whereareiam.socialismus.module.bubbler.api.VersionResolver;
import me.whereareiam.socialismus.type.Version;

import java.util.List;
import java.util.Map;

/**
 * Metadata packet for configuring display entity interpolation settings.
 * Interpolation allows smooth transitions between transform states.
 */
@Getter
@SuperBuilder
public class InterpolationMetadataPacket implements Packet {
	private final int entityId;
	private final int startDelayTicks;
	private final int transformDurationTicks;
	private final int positionRotationDurationTicks;

	@Override
	public void send(User user) {
		user.sendPacket(new WrapperPlayServerEntityMetadata(entityId, createMetadata()));
	}

	private List<EntityData<?>> createMetadata() {
		Map<Version, Integer> startDelayIndex = Map.of(
				Version.V_1_20_2, 8,
				Version.V_1_19_4, 7
		);
		Map<Version, Integer> transformDurationIndex = Map.of(
				Version.V_1_20_2, 9,
				Version.V_1_19_4, 8
		);
		Map<Version, Integer> positionRotationDurationIndex = Map.of(
				Version.V_1_20_2, 10,
				Version.V_1_19_4, 9
		);

		Integer startDelayIdx = VersionResolver.resolve(startDelayIndex, null);
		if (startDelayIdx == null) return List.of();

		return List.of(
				new EntityData<>(startDelayIdx, EntityDataTypes.INT, startDelayTicks),
				new EntityData<>(
						VersionResolver.resolveOrThrow(transformDurationIndex),
						EntityDataTypes.INT,
						transformDurationTicks
				),
				new EntityData<>(
						VersionResolver.resolveOrThrow(positionRotationDurationIndex),
						EntityDataTypes.INT,
						positionRotationDurationTicks
				)
		);
	}
}
