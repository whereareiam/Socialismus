package me.whereareiam.socialismus.testing.sync;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.model.requirement.type.ServerRequirement;
import me.whereareiam.socialismus.testing.environment.Container;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.Network;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Console;
import me.whereareiam.socialismus.testing.journey.Player;
import me.whereareiam.socialismus.type.requirement.RequirementConditionType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static me.whereareiam.socialismus.testing.environment.ServerFiles.CHAT_MESSAGES;
import static me.whereareiam.socialismus.testing.environment.ServerFiles.CHAT_SETTINGS;
import static me.whereareiam.socialismus.testing.environment.ServerFiles.MESSAGES;
import static me.whereareiam.socialismus.testing.environment.ServerFiles.chat;
import static me.whereareiam.socialismus.testing.environment.ServerFiles.meets;
import static me.whereareiam.socialismus.testing.environment.ServerFiles.requirements;

/**
 * Who handles a player's chat when SocialismusNetwork runs on a proxy and on servers behind it: the server the player
 * stands on when it runs Socialismus, else the proxy, and never both.
 */
@Tag("sync")
class ChatOwnershipTest {
	private static final String LOBBY_ONLY = "This chat stays on {servers}";
	private static final Duration LEASE = Duration.ofSeconds(4);

	@Test
	@Socialismus(
			network = Network.PROXY_AND_BACKENDS,
			containers = Container.REDIS,
			modules = {Module.NETWORK, Module.REDIS},
			configure = {GlobalAndLocalChat.class, ThroughRedis.class}
	)
	void aProxyAndItsServersShowEachMessageExactlyOnce(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob").switchesTo(Network.SURVIVAL);

		alice.chat("!once");
		alice.sees("[G] Alice: once");
		bob.sees("[G] Alice: once");
		alice.neverSees("once");
		bob.neverSees("once");
	}

	@Test
	@Socialismus(
			network = Network.PROXY_AND_LOBBY,
			containers = Container.REDIS,
			modules = {Module.NETWORK, Module.REDIS},
			configure = {GlobalAndLocalChat.class, ThroughRedis.class}
	)
	void theProxyHandlesTheChatOfAServerWithoutSocialismus(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob").switchesTo(Network.SURVIVAL);

		bob.chat("!from survival");
		bob.sees("[G] Bob: from survival");
		alice.sees("[G] Bob: from survival");
		bob.neverSees("from survival");

		alice.chat("!from the lobby");
		bob.sees("[G] Alice: from the lobby");
		bob.neverSees("from the lobby");
	}

	@Test
	@Socialismus(
			network = Network.PROXY_AND_BACKENDS,
			containers = Container.REDIS,
			modules = {Module.NETWORK, Module.REDIS},
			configure = {GlobalAndLocalChat.class, ThroughRedis.class, ShortLease.class}
	)
	void theProxyTakesOverTheChatOfAServerThatStopsAnsweringAndGivesItBackOnceItAnswers(ScenarioContext anvil)
			throws InterruptedException {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob").switchesTo(Network.SURVIVAL);
		Console.Output proxy = Console.of(anvil, Network.PROXY).watch();

		anvil.processes().freeze(Network.LOBBY);
		proxy.printed("left the network");
		// The lobby's claims on its players expired with its announcement; one renewal of the proxy settles them.
		Thread.sleep(LEASE.dividedBy(3).toMillis());

		alice.chat("!the lobby hangs");
		bob.sees("[G] Alice: the lobby hangs");
		alice.sees("[G] Alice: the lobby hangs");

		anvil.processes().thaw(Network.LOBBY);
		proxy.printed("joined the network");
		Thread.sleep(LEASE.dividedBy(3).toMillis());

		alice.chat("!the lobby is back");
		bob.sees("[G] Alice: the lobby is back");
		bob.neverSees("the lobby is back");
	}

	@Test
	@Socialismus(
			network = Network.PROXY_AND_BACKENDS,
			containers = Container.REDIS,
			modules = {Module.NETWORK, Module.REDIS},
			configure = {LobbyChat.class, ThroughRedis.class}
	)
	void aServerLearnsItsNameFromTheProxySoAServerRequirementJudgesItsPlayers(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob").switchesTo(Network.SURVIVAL);

		alice.chat("%hello lobby");
		alice.sees("[Lobby] Alice: hello lobby");

		bob.chat("%let me in");
		bob.sees("This chat stays on lobby");
	}

	/**
	 * Every node's announcement and claims live four seconds without renewal, so a node that stops answering is
	 * taken over soon.
	 */
	static final class ShortLease implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.put("modules/SocialismusNetwork/settings.yml", Map.of("lease", LEASE.toSeconds() + "s"));
		}
	}

	/**
	 * A message starting with {@code %} goes to lobby chat, which only the players on the lobby send in; every other
	 * one to global chat. A player elsewhere is told that lobby chat stays on the lobby.
	 */
	static final class LobbyChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			RequirementGroup lobby = meets(Constants.Requirements.SERVER, ServerRequirement.builder()
					.condition(RequirementConditionType.CONTAINS)
					.expected("true")
					.servers(List.of(Network.LOBBY))
					.build());
			files.chats(
					chat("global", 0, "", 0, "[G] {playerName}: {message}"),
					chat("lobby", 1, "%", 0, "[Lobby] {playerName}: {message}").toBuilder()
							.requirements(requirements(lobby, null))
							.build()
			);
			// Without a fallback chat, a refused sender is told why, in messages.yml's wording.
			files.put(CHAT_SETTINGS, Map.of("fallback", Map.of("enabled", false, "chat", "fallback")));
			files.put(CHAT_MESSAGES, Map.of("requirements", Map.of("SENDER", Map.of("socialismus:server", Map.of("required", "")))));
			files.put(MESSAGES, Map.of("requirements", Map.of("socialismus:server", Map.of("required", LOBBY_ONLY))));
		}
	}
}
