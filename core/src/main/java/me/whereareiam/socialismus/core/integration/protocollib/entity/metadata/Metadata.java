package me.whereareiam.socialismus.core.integration.protocollib.entity.metadata;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public abstract class Metadata {
	protected final WrappedDataWatcher metadata = new WrappedDataWatcher();

	public abstract WrappedDataWatcher getMetadata();
}