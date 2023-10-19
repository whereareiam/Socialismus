package me.whereareiam.socialismus.integration.protocollib.entity.metadata;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import java.util.ArrayList;
import java.util.List;

public class EntityMetadataPacket extends Metadata {
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

    public PacketContainer createMetadataPacket(int id, WrappedDataWatcher metadata) {
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metaPacket.getIntegers().write(0, id);
        try {
            final List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();

            for (final WrappedWatchableObject entry : metadata.getWatchableObjects()) {
                if (entry == null) continue;

                final WrappedDataWatcher.WrappedDataWatcherObject watcherObject = entry.getWatcherObject();
                wrappedDataValueList.add(
                        new WrappedDataValue(
                                watcherObject.getIndex(),
                                watcherObject.getSerializer(),
                                entry.getRawValue()
                        )
                );
            }

            metaPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        } catch (Throwable e) {
            metaPacket.getWatchableCollectionModifier().write(0, metadata.getWatchableObjects());
        }
        return metaPacket;
    }
}