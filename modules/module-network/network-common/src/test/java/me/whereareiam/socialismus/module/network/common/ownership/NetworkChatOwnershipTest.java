package me.whereareiam.socialismus.module.network.common.ownership;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.network.api.type.NodeRole;
import me.whereareiam.socialismus.module.network.common.DefaultNetworkService;
import me.whereareiam.socialismus.module.network.common.TestNetwork;
import org.junit.jupiter.api.Test;

import static me.whereareiam.socialismus.module.network.common.TestPlayers.player;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetworkChatOwnershipTest {
	private final TestNetwork network = new TestNetwork();

	@Test
	void aPlayerNobodyClaimedIsHandled() {
		assertTrue(ownership(network.node("lobby").service()).handles(player("Alice", null)));
	}

	@Test
	void theServerHandlesItsPlayerInsteadOfTheProxy() {
		DefaultNetworkService proxyService = network.node("proxy", NodeRole.PROXY, null).service();
		DefaultNetworkService lobbyService = network.node("lobby").service();
		NetworkChatOwnership proxy = ownership(proxyService);
		NetworkChatOwnership lobby = ownership(lobbyService);
		SocialismusPlayer atProxy = player("Alice", "lobby");
		SocialismusPlayer atLobby = player("Alice", null);

		proxy.review(atProxy);
		lobby.review(atLobby);

		assertTrue(lobby.handles(atLobby));
		assertFalse(proxy.handles(atProxy));
	}

	@Test
	void theProxyLeavesThePlayersOfAServerThatRunsSocialismus() {
		DefaultNetworkService proxyService = network.node("proxy", NodeRole.PROXY, null).service();
		DefaultNetworkService lobbyService = network.node("lobby").service();
		lobbyService.directory().update(lobbyService.self().toBuilder().server("lobby").build());
		NetworkChatOwnership proxy = ownership(proxyService);
		SocialismusPlayer alice = player("Alice", "lobby");

		proxy.review(alice);

		assertTrue(proxyService.leases(NetworkChatOwnership.LEASES).holder(alice.getUniqueId().toString()).isEmpty());
	}

	@Test
	void aServerThatLeavesChatToTheProxyClaimsNothingAndTheProxyHandlesItsPlayers() {
		DefaultNetworkService proxyService = network.node("proxy", NodeRole.PROXY, null).service();
		DefaultNetworkService lobbyService = network.node("lobby").service();
		lobbyService.directory().update(lobbyService.self().toBuilder().server("lobby").handlesChat(false).build());
		NetworkChatOwnership proxy = ownership(proxyService);
		NetworkChatOwnership lobby = ownership(lobbyService);
		SocialismusPlayer atProxy = player("Alice", "lobby");
		SocialismusPlayer atLobby = player("Alice", null);

		lobby.review(atLobby);
		proxy.review(atProxy);

		assertTrue(proxy.handles(atProxy));
		assertFalse(lobby.handles(atLobby));
	}

	@Test
	void theProxyHandlesThePlayersOfAServerWithoutSocialismus() {
		DefaultNetworkService proxyService = network.node("proxy", NodeRole.PROXY, null).service();
		NetworkChatOwnership proxy = ownership(proxyService);
		SocialismusPlayer bob = player("Bob", "minigames");

		proxy.review(bob);

		assertTrue(proxy.handles(bob));
	}

	@Test
	void theProxyTakesOverThePlayersOfAServerThatCrashed() {
		TestNetwork.Node proxyNode = network.node("proxy", NodeRole.PROXY, null);
		TestNetwork.Node lobbyNode = network.node("lobby");
		NetworkChatOwnership proxy = ownership(proxyNode.service());
		NetworkChatOwnership lobby = ownership(lobbyNode.service());
		SocialismusPlayer atProxy = player("Alice", "lobby");
		lobby.review(player("Alice", null));
		proxy.review(atProxy);
		assertFalse(proxy.handles(atProxy));

		lobbyNode.transport().disconnect();
		network.clock.advance(TestNetwork.LIFETIME.plusSeconds(1));
		proxyNode.service().maintain();
		proxy.review(atProxy);

		assertTrue(proxy.handles(atProxy));
	}

	private static NetworkChatOwnership ownership(DefaultNetworkService service) {
		return new NetworkChatOwnership(service.leases(NetworkChatOwnership.LEASES), service::self, service::nodes);
	}
}
