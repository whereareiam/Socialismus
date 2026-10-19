package me.whereareiam.socialismus.module.channelizer.network;

import me.whereareiam.socialismus.model.resource.ProvidedResource;
import me.whereareiam.socialismus.module.channelizer.api.PlatformMessageBus;
import me.whereareiam.socialismus.module.network.api.NetworkResources;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * SocialismusChannelizer as SocialismusNetwork's transport. Only touched while SocialismusNetwork is installed, since
 * its API is reachable only then. Plugin messages cannot store, so it offers no store.
 */
public final class ChannelizerNetworkResources {
	private ChannelizerNetworkResources() {
	}

	/**
	 * Returns the resources SocialismusNetwork replicates through.
	 */
	public static @NotNull List<ProvidedResource<?>> provide(@NotNull PlatformMessageBus bus) {
		return List.of(ProvidedResource.of(NetworkResources.TRANSPORT, new ChannelizerReplicationTransport(bus)));
	}
}
