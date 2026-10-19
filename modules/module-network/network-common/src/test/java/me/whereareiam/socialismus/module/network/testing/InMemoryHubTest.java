package me.whereareiam.socialismus.module.network.testing;

import me.whereareiam.socialismus.module.network.api.transport.ReplicationTransport;

class InMemoryHubTest extends ReplicationTransportContract {
	private final InMemoryHub hub = new InMemoryHub();

	@Override
	protected ReplicationTransport transport(String node) {
		return hub.transport(node);
	}
}
