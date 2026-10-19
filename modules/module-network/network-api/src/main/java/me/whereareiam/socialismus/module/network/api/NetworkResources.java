package me.whereareiam.socialismus.module.network.api;

import me.whereareiam.socialismus.model.resource.ResourceKey;
import me.whereareiam.socialismus.module.network.api.transport.ReplicationStore;
import me.whereareiam.socialismus.module.network.api.transport.ReplicationTransport;

/**
 * The resources SocialismusNetwork offers and takes. A transport module registers {@link #TRANSPORT}, and
 * {@link #STORE} when it can store; SocialismusNetwork registers {@link #NETWORK} for the modules that use it.
 *
 * <pre>{@code
 * public List<ProvidedResource<?>> provideResources() {
 *     return List.of(ProvidedResource.of(NetworkResources.TRANSPORT, transport));
 * }
 * }</pre>
 */
public final class NetworkResources {
	/**
	 * The network, for modules that share state or messages over it.
	 */
	public static final ResourceKey<NetworkService> NETWORK =
			ResourceKey.create("socialismusnetwork", "network", NetworkService.class);

	/**
	 * What carries messages between nodes; SocialismusNetwork uses the first one registered.
	 */
	public static final ResourceKey<ReplicationTransport> TRANSPORT =
			ResourceKey.create("socialismusnetwork", "transport", ReplicationTransport.class);

	/**
	 * Where replicated caches are stored, when the transport can store.
	 */
	public static final ResourceKey<ReplicationStore> STORE =
			ResourceKey.create("socialismusnetwork", "store", ReplicationStore.class);

	private NetworkResources() {
	}
}
