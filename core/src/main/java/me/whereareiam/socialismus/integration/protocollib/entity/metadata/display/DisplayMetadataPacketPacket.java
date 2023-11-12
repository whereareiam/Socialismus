package me.whereareiam.socialismus.integration.protocollib.entity.metadata.display;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import me.whereareiam.socialismus.Version;
import me.whereareiam.socialismus.integration.protocollib.entity.metadata.EntityMetadataPacket;
import me.whereareiam.socialismus.integration.protocollib.entity.metadata.display.type.DisplayType;
import org.joml.Vector3f;

public class DisplayMetadataPacketPacket extends EntityMetadataPacket {
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
                    getScaleIndex(),
                    WrappedDataWatcher.Registry.get(Vector3f.class)
            ), scale);
        }

        if (displayType != null) {
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(
                    getDisplayTypeIndex(),
                    WrappedDataWatcher.Registry.get(Byte.class)
            ), displayType.getValue());
        }
        return metadata;
    }

    private int getScaleIndex() {
        if (Version.isVersionBetween(Version.V1_19_4, Version.V1_20_1))
            return 11;

        if (Version.isVersionBetween(Version.V1_20_2, Version.V1_20_3))
            return 12;

        throw new UnsupportedOperationException("Version " + Version.getVersion() + " isn't supported");
    }

    private int getDisplayTypeIndex() {
        if (Version.isVersionBetween(Version.V1_19_4, Version.V1_20_1))
            return 14;

        if (Version.isVersionBetween(Version.V1_20_2, Version.V1_20_3))
            return 15;

        throw new UnsupportedOperationException("Version " + Version.getVersion() + " isn't supported");
    }
}
