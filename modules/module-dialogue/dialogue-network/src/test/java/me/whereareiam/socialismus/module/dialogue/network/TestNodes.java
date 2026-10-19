package me.whereareiam.socialismus.module.dialogue.network;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.network.api.model.NetworkNode;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import me.whereareiam.socialismus.module.network.api.type.NodeRole;
import me.whereareiam.socialismus.module.network.common.DefaultNetworkService;
import me.whereareiam.socialismus.module.network.common.replication.DefaultReplicationSystem;
import me.whereareiam.socialismus.module.network.testing.InMemoryHub;
import me.whereareiam.socialismus.module.network.testing.JsonSerialization;
import me.whereareiam.socialismus.module.network.testing.MutableClock;

import java.time.Duration;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Network nodes in memory for Dialogue's tests, sharing one clock; everything runs on the test's thread.
 */
public final class TestNodes {
	public final InMemoryHub hub = new InMemoryHub();
	public final MutableClock clock = new MutableClock();

	static {
		Logger.init(mock(LoggingHelper.class));
	}

	public DefaultNetworkService node(String name) {
		NodeId id = NodeId.of(name);
		DefaultReplicationSystem replication = new DefaultReplicationSystem(id, clock, new JsonSerialization(), Runnable::run);
		DefaultNetworkService network = new DefaultNetworkService(replication,
				NetworkNode.builder().id(id).role(NodeRole.SERVER).version("dev").build(), Duration.ofSeconds(15));
		network.attach(hub.transport(name), null);

		return network;
	}

	public static SocialismusPlayer player(String name) {
		SocialismusPlayer player = mock(SocialismusPlayer.class);
		when(player.getUniqueId()).thenReturn(UUID.nameUUIDFromBytes(name.getBytes()));
		when(player.getUsername()).thenReturn(name);

		return player;
	}
}
