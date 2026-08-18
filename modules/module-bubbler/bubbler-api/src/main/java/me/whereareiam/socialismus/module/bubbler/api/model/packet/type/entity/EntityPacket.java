package me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.Packet;
import me.whereareiam.socialismus.type.Version;

import java.util.List;

/**
 * Abstract base class for entity spawn packets.
 * Provides common entity properties like ID, position, and gravity settings.
 */
@Getter
@SuperBuilder(toBuilder = true)
public abstract class EntityPacket implements Packet {
	@Builder.Default
	protected int entityId = (int) (Math.random() * Integer.MAX_VALUE);
	@Builder.Default
	protected final Vector3d position = new Vector3d(0.0F, 0.0F, 0.0F);
	protected final boolean noGravity;

	/**
	 * Adds common entity metadata to the metadata list.
	 * Handles version-specific differences in the no-gravity flag.
	 *
	 * @param metadata the metadata list to add to
	 */
	protected void addCommonMetadata(List<EntityData<?>> metadata) {
		if (Constants.SERVER_VERSION.isAtLeast(Version.V_1_19_4)) {
			metadata.add(new EntityData<>(5, EntityDataTypes.BOOLEAN, noGravity));
		} else {
			metadata.add(new EntityData<>(5, EntityDataTypes.BYTE, (byte) (noGravity ? 0x02 : 0)));
		}
	}
}
