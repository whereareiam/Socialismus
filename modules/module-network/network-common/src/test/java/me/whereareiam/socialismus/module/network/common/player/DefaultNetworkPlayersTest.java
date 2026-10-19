package me.whereareiam.socialismus.module.network.common.player;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.network.api.player.NetworkPlayer;
import me.whereareiam.socialismus.module.network.api.type.NodeRole;
import me.whereareiam.socialismus.module.network.common.TestNetwork;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.whereareiam.socialismus.module.network.common.TestPlayers.player;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultNetworkPlayersTest {
	private final TestNetwork network = new TestNetwork();

	@Test
	void aNodeKnowsThePlayersTheOthersHostButNotItsOwn() {
		DefaultNetworkPlayers lobby = network.node("lobby").service().players();
		DefaultNetworkPlayers survival = network.node("survival").service().players();
		SocialismusPlayer alice = player("Alice", null);
		lobby.host(alice);

		assertTrue(lobby.players().isEmpty());
		NetworkPlayer found = survival.find("alice").orElseThrow();
		assertEquals(alice.getUniqueId(), found.getUniqueId());
		assertEquals("lobby", found.getNode().value());
	}

	@Test
	void aPlayerWhoLeftIsForgotten() {
		DefaultNetworkPlayers lobby = network.node("lobby").service().players();
		DefaultNetworkPlayers survival = network.node("survival").service().players();
		SocialismusPlayer alice = player("Alice", null);
		lobby.host(alice);

		lobby.unhost(alice.getUniqueId());

		assertTrue(survival.find("Alice").isEmpty());
	}

	@Test
	void thePlayersOfANodeThatStopsAreForgottenWithinOneLifetime() {
		TestNetwork.Node lobby = network.node("lobby");
		TestNetwork.Node survival = network.node("survival");
		lobby.service().players().host(player("Alice", null));

		lobby.transport().disconnect();
		network.clock.advance(TestNetwork.LIFETIME.plusSeconds(1));
		survival.service().maintain();

		assertTrue(survival.service().players().find("Alice").isEmpty());
	}

	@Test
	void theServerAnnouncesAPlayerTheProxyHostsToo() {
		DefaultNetworkPlayers proxy = network.node("proxy", NodeRole.PROXY, null).service().players();
		DefaultNetworkPlayers lobby = network.node("lobby").service().players();
		DefaultNetworkPlayers survival = network.node("survival").service().players();
		proxy.host(player("Alice", "lobby"));
		lobby.host(player("Alice", null));

		List<NetworkPlayer> seen = List.copyOf(survival.players());

		assertEquals(1, seen.size());
		assertEquals("lobby", seen.getFirst().getNode().value());
	}

	@Test
	void aServerLearnsItsNameFromTheProxy() {
		DefaultNetworkPlayers proxy = network.node("proxy", NodeRole.PROXY, null).service().players();
		DefaultNetworkPlayers lobby = network.node("lobby").service().players();
		SocialismusPlayer alice = player("Alice", "lobby-1");
		proxy.host(alice);

		assertEquals(Optional.of("lobby-1"), lobby.serverByProxy(alice.getUniqueId()));
	}

	@Test
	void aServerHearsTheServerTheProxyAnnouncesAsItArrives() {
		DefaultNetworkPlayers proxy = network.node("proxy", NodeRole.PROXY, null).service().players();
		DefaultNetworkPlayers lobby = network.node("lobby").service().players();
		List<String> heard = new ArrayList<>();
		lobby.onServerByProxy((player, server) -> heard.add(server));
		SocialismusPlayer alice = player("Alice", "lobby-1");

		lobby.host(player("Bob", null));
		proxy.host(alice);

		assertEquals(List.of("lobby-1"), heard);
	}
}
