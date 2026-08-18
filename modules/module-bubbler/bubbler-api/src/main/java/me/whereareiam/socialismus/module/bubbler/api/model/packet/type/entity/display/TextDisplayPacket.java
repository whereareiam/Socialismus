package me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.bubbler.api.VersionResolver;
import me.whereareiam.socialismus.module.bubbler.api.type.AlignmentType;
import me.whereareiam.socialismus.type.Version;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Packet for spawning a text display entity (1.19.4+).
 * Text displays provide rich text rendering with customizable styling,
 * background, and alignment options.
 */
@Getter
@SuperBuilder(toBuilder = true)
public class TextDisplayPacket extends DisplayPacket {
	private final Component text;
	private final boolean hasShadow;
	private final boolean isSeeThrough;
	private final AlignmentType alignment;
	private final int backgroundColor;
	private final short transparency;

	@Override
	public void send(User user) {
		WrapperPlayServerSpawnEntity spawnPacket = createSpawnPacket();
		WrapperPlayServerEntityMetadata metadataPacket = createMetadataPacket();

		user.sendPacket(spawnPacket);
		user.sendPacket(metadataPacket);
	}

	private WrapperPlayServerSpawnEntity createSpawnPacket() {
		return new WrapperPlayServerSpawnEntity(
				entityId, Optional.of(UUID.randomUUID()), EntityTypes.TEXT_DISPLAY,
				position, 0.0F, 0.0F, 0.0F, 0, Optional.empty()
		);
	}

	private WrapperPlayServerEntityMetadata createMetadataPacket() {
		List<EntityData<?>> metadata = new ArrayList<>();
		addDisplayMetadata(metadata);

		Map<Version, Integer> textIndex = Map.of(
				Version.V_1_20_2, 23,
				Version.V_1_19_4, 22
		);
		Map<Version, Integer> backgroundColorIndex = Map.of(
				Version.V_1_20_2, 25,
				Version.V_1_19_4, 24
		);
		Map<Version, Integer> transparencyIndex = Map.of(
				Version.V_1_20_2, 26,
				Version.V_1_19_4, 25
		);
		Map<Version, Integer> flagsIndex = Map.of(
				Version.V_1_20_2, 27,
				Version.V_1_19_4, 26
		);

		metadata.add(new EntityData<>(
				VersionResolver.resolveOrThrow(textIndex),
				EntityDataTypes.ADV_COMPONENT,
				text
		));
		metadata.add(new EntityData<>(
				VersionResolver.resolveOrThrow(backgroundColorIndex),
				EntityDataTypes.INT,
				backgroundColor
		));
		metadata.add(new EntityData<>(
				VersionResolver.resolveOrThrow(transparencyIndex),
				EntityDataTypes.BYTE,
				(byte) (255 - (transparency * 255) / 100)
		));

		byte flags = 0;
		if (hasShadow) flags |= 0x01;
		if (isSeeThrough) flags |= 0x02;
		if (alignment != null) flags |= alignment.getValue();

		metadata.add(new EntityData<>(
				VersionResolver.resolveOrThrow(flagsIndex),
				EntityDataTypes.BYTE,
				flags
		));

		return new WrapperPlayServerEntityMetadata(entityId, metadata);
	}
}
