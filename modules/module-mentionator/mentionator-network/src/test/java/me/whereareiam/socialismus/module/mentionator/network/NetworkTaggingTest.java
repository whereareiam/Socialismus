package me.whereareiam.socialismus.module.mentionator.network;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.mentionator.common.notify.Notifier;
import me.whereareiam.socialismus.module.mentionator.common.notify.Ping;
import me.whereareiam.socialismus.module.mentionator.common.notify.SoundCue;
import me.whereareiam.socialismus.module.mentionator.common.notify.TagDelivery;
import me.whereareiam.socialismus.module.network.api.model.NetworkNode;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import me.whereareiam.socialismus.module.network.api.type.NodeRole;
import me.whereareiam.socialismus.module.network.common.DefaultNetworkService;
import me.whereareiam.socialismus.module.network.common.replication.DefaultReplicationSystem;
import me.whereareiam.socialismus.module.network.testing.InMemoryHub;
import me.whereareiam.socialismus.module.network.testing.JsonSerialization;
import me.whereareiam.socialismus.module.network.testing.MutableClock;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tagged players on other nodes of an in-memory network, each node with its own Mentionator around a mocked notifier.
 */
class NetworkTaggingTest {
	private final InMemoryHub hub = new InMemoryHub();
	private final MutableClock clock = new MutableClock();
	private final SocialismusPlayer alice = player("Alice");
	private final SocialismusPlayer bob = player("Bob");

	@BeforeAll
	static void initLogger() {
		Logger.init(mock(LoggingHelper.class));
	}

	@Test
	void aPlayerOfAnotherNodeMayBeTagged() {
		Side lobby = side("lobby");
		Side survival = side("survival");
		survival.network.players().host(bob);

		assertTrue(lobby.tagging.players().stream().anyMatch(player -> player.getUniqueId().equals(bob.getUniqueId())));
	}

	@Test
	void theNodeOfATaggedPlayerNotifiesThem() {
		Side lobby = side("lobby");
		Side survival = side("survival");
		Side creative = side("creative");
		survival.network.players().host(bob);
		when(survival.registry.getPlayerData(bob.getUniqueId())).thenReturn(Optional.of(bob));
		TagDelivery delivery = new TagDelivery(
				alice.getUniqueId(),
				"Alice",
				"lobby",
				"global",
				"hi @Bob",
				false,
				List.of(new Ping("player", "Bob", List.of("ping"), true, false))
		);

		lobby.tagging.send(bob.getUniqueId(), delivery);

		ArgumentCaptor<SocialismusPlayer> author = ArgumentCaptor.forClass(SocialismusPlayer.class);
		verify(survival.notifier).deliver(eq(bob), author.capture(), eq(delivery));
		assertEquals("Alice", author.getValue().getUsername());
		verify(creative.notifier, never()).deliver(any(), any(), any());
		verify(lobby.notifier, never()).deliver(any(), any(), any());
	}

	@Test
	void aProxyHandsSoundsToTheServerOfThePlayer() {
		Side proxy = side("proxy");
		Side survival = side("survival");
		survival.network.players().host(bob);
		List<SoundCue> sounds = List.of(new SoundCue("minecraft:block.note_block.pling", 1.0f, 1.4f));

		proxy.tagging.playSounds(bob.getUniqueId(), sounds);

		verify(survival.notifier).playSounds(bob.getUniqueId(), sounds);
	}

	@Test
	void aNodeThatLeftTakesNoMoreRecords() {
		Side lobby = side("lobby");
		Side survival = side("survival");
		survival.network.players().host(bob);
		survival.tagging.close();

		lobby.tagging.playSounds(bob.getUniqueId(), List.of(new SoundCue("minecraft:block.note_block.pling", 1.0f, 1.0f)));

		verify(survival.notifier, never()).playSounds(any(), any());
	}

	private Side side(String name) {
		NodeId id = NodeId.of(name);
		DefaultReplicationSystem replication = new DefaultReplicationSystem(id, clock, new JsonSerialization(), Runnable::run);
		DefaultNetworkService network = new DefaultNetworkService(replication,
				NetworkNode.builder().id(id).role(NodeRole.SERVER).version("dev").build(), Duration.ofSeconds(15));
		network.attach(hub.transport(name), null);

		Notifier notifier = mock(Notifier.class);
		PlayerRegistry registry = mock(PlayerRegistry.class);
		when(registry.getPlayerData(any(UUID.class))).thenReturn(Optional.empty());

		return new Side(network, new NetworkTagging(network, notifier, registry), notifier, registry);
	}

	private static SocialismusPlayer player(String name) {
		SocialismusPlayer player = mock(SocialismusPlayer.class);
		when(player.getUniqueId()).thenReturn(UUID.nameUUIDFromBytes(name.getBytes()));
		when(player.getUsername()).thenReturn(name);

		return player;
	}

	private record Side(DefaultNetworkService network, NetworkTagging tagging, Notifier notifier, PlayerRegistry registry) {
	}
}
