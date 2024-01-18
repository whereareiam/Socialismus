package me.whereareiam.socialismus.core.integration.protocollib.entity.metadata.display;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.inject.Inject;
import me.whereareiam.socialismus.core.integration.protocollib.ProtocolVersion;
import me.whereareiam.socialismus.core.integration.protocollib.entity.metadata.EntityMetadataPacket;
import me.whereareiam.socialismus.core.integration.protocollib.entity.metadata.display.type.DisplayType;
import org.joml.Vector3f;

public class DisplayMetadataPacket extends EntityMetadataPacket {
	@Inject
	private ProtocolVersion protocolVersion;
	private Vector3f scale;
	private DisplayType displayType;

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}

	public void setDisplayType(DisplayType displayType) {
		this.displayType = displayType;
	}

	@Override
	public WrappedDataWatcher getMetadata() {
		super.getMetadata();

		if (scale != null) {
			metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(
					protocolVersion.getMetaScale(),
					WrappedDataWatcher.Registry.get(Vector3f.class)
			), scale);
		}

		if (displayType != null) {
			metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(
					protocolVersion.getMetaDisplayType(),
					WrappedDataWatcher.Registry.get(Byte.class)
			), displayType.getValue());
		}
		return metadata;
	}
}
