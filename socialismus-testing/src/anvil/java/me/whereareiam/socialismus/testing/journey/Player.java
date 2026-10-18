package me.whereareiam.socialismus.testing.journey;

import me.whereareiam.anvil.api.model.player.PlayerLogin;
import me.whereareiam.anvil.api.model.player.PlayerOptions;
import me.whereareiam.anvil.api.player.SimulatedPlayer;
import me.whereareiam.anvil.api.process.ProcessConsole;
import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.anvil.capability.messages.MessageWatch;
import me.whereareiam.anvil.capability.messages.Messages;
import me.whereareiam.anvil.capability.players.PlayerWatch;
import me.whereareiam.anvil.capability.players.Players;
import me.whereareiam.anvil.capability.session.Session;
import me.whereareiam.socialismus.testing.environment.Network;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.function.Predicate;

/**
 * A player of a journey: joins the network, chats, runs commands and expects what reaches it. What it expects
 * is looked for among the messages it received since its previous expectation, so an earlier message never
 * satisfies a later step.
 *
 * <pre>{@code
 * Player alice = Player.joins(anvil, "Alice");
 * Player bob = Player.joins(anvil, "Bob");
 * alice.chat("hello");
 * bob.sees("[G] Alice: hello");
 * }</pre>
 */
public final class Player {
	private static final Duration TIMEOUT = Duration.ofSeconds(15);
	/** How long a player waits for a message that must not arrive. */
	private static final Duration ABSENCE = Duration.ofSeconds(3);
	/** How often the readiness message is sent, a second apart, before a join counts as failed. */
	private static final int READINESS_ATTEMPTS = 15;

	private final ScenarioContext anvil;
	private final String name;
	private final Messages messages;
	/** The player's messages after the one its previous expectation found, where its next expectation looks. */
	private final MessageWatch inbox;

	private Player(ScenarioContext anvil, String name, SimulatedPlayer player) {
		this.anvil = anvil;
		this.name = name;
		this.messages = player.capability(Messages.class);
		this.inbox = messages.watch();
	}

	/**
	 * Creates an offline player and joins the network with it, through the proxy on the Velocity network.
	 *
	 * @param anvil the running network
	 * @param name  the player's name
	 * @return the player, standing on the network's server
	 */
	@NotNull
	public static Player joins(@NotNull ScenarioContext anvil, @NotNull String name) {
		SimulatedPlayer player = anvil.players().create(PlayerOptions.builder()
				.name(name)
				.login(PlayerLogin.offline(name))
				.build());
		Player journey = new Player(anvil, name, player);
		PlayerWatch arrivals = anvil.processes().server(Network.SERVER).capability(Players.class).watch();

		player.capability(Session.class).connect();
		arrivals.joined(player, TIMEOUT);
		journey.takesMessages();

		return journey;
	}

	/**
	 * Sends a chat message.
	 */
	@NotNull
	public Player chat(@NotNull String message) {
		messages.chat(message);
		return this;
	}

	/**
	 * Runs a command, with or without its slash.
	 */
	@NotNull
	public Player command(@NotNull String command) {
		messages.command(command);
		return this;
	}

	/**
	 * Expects a message that contains the given text.
	 *
	 * @return the whole message
	 */
	@NotNull
	public String sees(@NotNull String text) {
		return received("\"" + text + "\"", message -> message.contains(text));
	}

	/**
	 * Expects a message that contains every given text.
	 *
	 * @return the whole message
	 */
	@NotNull
	public String seesAll(@NotNull String... texts) {
		return received(String.join(" and ", texts), message -> {
			for (String text : texts)
				if (!message.contains(text)) return false;

			return true;
		});
	}

	/**
	 * Expects that no message containing the given text arrives for a few seconds.
	 */
	@NotNull
	public Player neverSees(@NotNull String text) {
		inbox.neverReceived(text, ABSENCE);
		return this;
	}

	/**
	 * Moves the player through the server's console, for a journey that needs players at a distance.
	 */
	@NotNull
	public Player teleport(int x, int y, int z) {
		Console.of(anvil).run("tp " + name + " " + x + " " + y + " " + z)
				.printed("Teleported " + name);
		return this;
	}

	/**
	 * Moves the player into another dimension through the server's console, for a journey that needs players in
	 * different worlds.
	 *
	 * @param dimension the dimension, such as {@code minecraft:the_nether}
	 */
	@NotNull
	public Player travelsTo(@NotNull String dimension) {
		Console.of(anvil).run("execute in " + dimension + " run tp " + name + " 0 100 0")
				.printed("Teleported " + name);
		return this;
	}

	/**
	 * Makes the player a server operator, which grants every permission the server does not deny it.
	 */
	@NotNull
	public Player op() {
		Console.of(anvil).run("op " + name).printed("Made " + name + " a server operator");
		return this;
	}

	/**
	 * Expects that the player can no longer scroll back to a line containing the given text. Anvil keeps the chat as
	 * the vanilla client does but cannot wrap its lines, so a line it no longer holds is gone from every client.
	 */
	@NotNull
	public Player chatNoLongerShows(@NotNull String text) {
		if (messages.chatLines().contains(text))
			throw new AssertionError(name + " can still scroll back to \"" + text + "\": " + messages.chatLines().getLines());

		return this;
	}

	/**
	 * Expects that the player can scroll back to a line containing the given text, on a chat wide enough not to wrap
	 * the lines after it.
	 */
	@NotNull
	public Player chatShows(@NotNull String text) {
		if (!messages.chatLines().contains(text))
			throw new AssertionError(name + " cannot scroll back to \"" + text + "\": " + messages.chatLines().getLines());

		return this;
	}

	@NotNull
	public String name() {
		return name;
	}

	/**
	 * Waits until the client takes messages. The server has the player once it joined, but the client may still be
	 * finishing its login, or switching to the backend behind a proxy, and loses what is sent meanwhile; a message
	 * the player's server sends it, again until it arrives, tells that it takes them.
	 */
	private void takesMessages() {
		String token = "journey-ready-" + name;
		ProcessConsole server = anvil.processes().server(Network.SERVER).console();
		for (int attempt = 0; attempt < READINESS_ATTEMPTS; attempt++) {
			server.sendCommand("tellraw " + name + " {\"text\":\"" + token + "\"}");
			try {
				inbox.received(token, Duration.ofSeconds(1));
				return;
			} catch (IllegalStateException notYet) {
				// The client did not take it yet; send it again.
			}
		}

		throw new AssertionError(name + " joined but did not take messages within " + READINESS_ATTEMPTS + " seconds");
	}

	private String received(String expected, Predicate<String> matcher) {
		try {
			return inbox.received(matcher, TIMEOUT);
		} catch (IllegalStateException failure) {
			throw new AssertionError(name + " did not receive " + expected + "; since its previous step it received "
					+ inbox.unread(), failure);
		}
	}
}
