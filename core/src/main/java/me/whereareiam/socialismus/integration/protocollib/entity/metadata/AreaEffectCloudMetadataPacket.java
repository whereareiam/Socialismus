package me.whereareiam.socialismus.integration.protocollib.entity.metadata;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.inject.Inject;
import me.whereareiam.socialismus.integration.protocollib.ProtocolVersion;

public class AreaEffectCloudMetadataPacket extends EntityMetadataPacket {
    @Inject
    private ProtocolVersion protocolVersion;
    private float radius;

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public WrappedDataWatcher getMetadata() {
        super.getMetadata();

        if (radius >= 0) {
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(
                    protocolVersion.getMetaRadius(),
                    WrappedDataWatcher.Registry.get(Float.class)
            ), radius);
        }

        return metadata;
    }
}