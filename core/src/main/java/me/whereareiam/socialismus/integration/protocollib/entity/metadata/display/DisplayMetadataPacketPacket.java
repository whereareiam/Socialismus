package me.whereareiam.socialismus.integration.protocollib.entity.metadata.display;

import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import me.whereareiam.socialismus.integration.protocollib.entity.metadata.EntityMetadataPacket;
import me.whereareiam.socialismus.integration.protocollib.entity.metadata.display.type.DisplayType;
import org.joml.Vector3f;

public class DisplayMetadataPacketPacket extends EntityMetadataPacket {
    private Vector3F scale;
    private DisplayType displayType;

    public void setScale(Vector3F scale) {
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
                    12,
                    WrappedDataWatcher.Registry.get(Vector3f.class)
            ), scale);
        }

        if (displayType != null) {
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(
                    15,
                    WrappedDataWatcher.Registry.get(Byte.class)
            ), displayType.getValue());
        }
        return metadata;
    }
}
