package me.whereareiam.socialismus.integration.protocollib.entity.metadata;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public class MobMetadataPacket extends LivingEntityMetadataPacket {
    private boolean hasAI = true;

    public void setHasAI(boolean hasAI) {
        this.hasAI = hasAI;
    }

    @Override
    public WrappedDataWatcher getMetadata() {
        super.getMetadata();

        if (!hasAI) {
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(
                    15,
                    WrappedDataWatcher.Registry.get(Byte.class)
            ), (byte) 0x01);
        }
        return metadata;
    }
}