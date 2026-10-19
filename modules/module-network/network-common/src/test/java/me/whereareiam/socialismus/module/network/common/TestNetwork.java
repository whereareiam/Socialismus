package me.whereareiam.socialismus.module.network.common;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.module.network.api.model.NetworkNode;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import me.whereareiam.socialismus.module.network.api.transport.ReplicationStore;
import me.whereareiam.socialismus.module.network.api.type.NodeRole;
import me.whereareiam.socialismus.module.network.common.replication.DefaultReplicationSystem;
import me.whereareiam.socialismus.module.network.testing.InMemoryHub;
import me.whereareiam.socialismus.module.network.testing.JsonSerialization;
import me.whereareiam.socialismus.module.network.testing.MutableClock;
import me.whereareiam.socialismus.service.SerializationService;

import java.time.Duration;

import static org.mockito.Mockito.mock;

/**
 * Nodes of one network in memory, sharing a clock and, when given, a store. Everything runs on the test's thread, so
 * a change has reached every connected node when the call that made it returns.
 */
public final class TestNetwork {
	public static final Duration LIFETIME = Duration.ofSeconds(15);

	public final InMemoryHub hub;
	private final SerializationService serialization;
	public final MutableClock clock = new MutableClock();

	static {
		Logger.init(mock(LoggingHelper.class));
	}

	public TestNetwork() {
		this(Integer.MAX_VALUE);
	}

	/**
	 * Creates a network whose transports carry messages up to the given size.
	 */
	public TestNetwork(int maxMessageSize) {
		this(maxMessageSize, new JsonSerialization());
	}

	/**
	 * Creates a network whose nodes serialize through the given service, such as Socialismus's own.
	 */
	public TestNetwork(int maxMessageSize, SerializationService serialization) {
		hub = new InMemoryHub(maxMessageSize);
		this.serialization = serialization;
	}

	/**
	 * Starts a node on the network.
	 *
	 * @param name  the node's id
	 * @param store the store every node shares, or {@code null} for none
	 */
	public Node node(String name, ReplicationStore store) {
		return node(name, NodeRole.SERVER, store);
	}

	/**
	 * Starts a node of the given role on the network.
	 */
	public Node node(String name, NodeRole role, ReplicationStore store) {
		DefaultReplicationSystem replication = new DefaultReplicationSystem(NodeId.of(name), clock, serialization, Runnable::run);
		NetworkNode self = NetworkNode.builder().id(NodeId.of(name)).role(role).version("dev").build();
		DefaultNetworkService service = new DefaultNetworkService(replication, self, LIFETIME);
		InMemoryHub.HubTransport transport = hub.transport(name);
		service.attach(transport, store);

		return new Node(service, transport);
	}

	public Node node(String name) {
		return node(name, null);
	}

	/**
	 * One node: its network service and its transport, which the test can take off the network.
	 */
	public record Node(DefaultNetworkService service, InMemoryHub.HubTransport transport) {
	}
}
