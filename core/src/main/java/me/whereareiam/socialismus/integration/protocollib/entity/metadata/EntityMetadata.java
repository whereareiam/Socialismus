package me.whereareiam.socialismus.integration.protocollib.entity.metadata;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public class EntityMetadata extends Metadata {
    private Boolean isVisible = true;

    public void setVisibility(boolean isVisible) {
        this.isVisible = isVisible;
    }

    @Override
    public WrappedDataWatcher getMetadata() {
        if (isVisible != null) {
            byte visibilityByte = (byte) (isVisible ? 0 : 0x20);
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(
                    0,
                    WrappedDataWatcher.Registry.get(Byte.class)
            ), visibilityByte);
        }
        return metadata;
    }
}