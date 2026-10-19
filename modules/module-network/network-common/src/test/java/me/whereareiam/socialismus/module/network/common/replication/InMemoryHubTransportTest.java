package me.whereareiam.socialismus.module.network.common.replication;

import me.whereareiam.socialismus.module.network.api.transport.ReplicationTransport;
import me.whereareiam.socialismus.module.network.testing.InMemoryHub;
import me.whereareiam.socialismus.module.network.testing.ReplicationTransportContract;

class InMemoryHubTransportTest extends ReplicationTransportContract {
	private final InMemoryHub hub = new InMemoryHub();

	@Override
	protected ReplicationTransport transport(String node) {
		return hub.transport(node);
	}
}
