package me.whereareiam.socialismus.integration.protocollib.entity.metadata;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public class LivingEntityMetadata extends EntityMetadata {
    @Override
    public WrappedDataWatcher getMetadata() {
        super.getMetadata();

        return metadata;
    }
}
