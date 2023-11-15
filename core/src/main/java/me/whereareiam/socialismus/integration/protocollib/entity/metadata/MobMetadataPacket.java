package me.whereareiam.socialismus.integration.protocollib.entity.metadata;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.inject.Inject;
import me.whereareiam.socialismus.integration.protocollib.ProtocolVersion;

public class MobMetadataPacket extends LivingEntityMetadataPacket {
    @Inject
    private ProtocolVersion protocolVersion;
    private boolean hasAI = true;

    public void setHasAI(boolean hasAI) {
        this.hasAI = hasAI;
    }

    @Override
    public WrappedDataWatcher getMetadata() {
        super.getMetadata();

        if (!hasAI) {
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(
                    protocolVersion.getMetaHasAI(),
                    WrappedDataWatcher.Registry.get(Byte.class)
            ), (byte) 0x01);
        }
        return metadata;
    }
}