package me.whereareiam.socialismus.testing.journey;

import me.whereareiam.anvil.api.model.player.PlayerLogin;
import me.whereareiam.anvil.api.model.player.PlayerOptions;
import me.whereareiam.anvil.api.player.SimulatedPlayer;
import me.whereareiam.anvil.api.process.ProcessConsole;
import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.anvil.capability.messages.Messages;
import me.whereareiam.anvil.capability.messages.model.ReceivedMessage;
import me.whereareiam.anvil.capability.server.Server;
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
	/** Position in the player's messages after which its next expectation looks. */
	private int checkpoint;

	private Player(ScenarioContext anvil, String name, SimulatedPlayer player) {
		this.anvil = anvil;
		this.name = name;
		this.messages = player.capability(Messages.class);
		this.checkpoint = messages.checkpoint();
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

		player.capability(Session.class).connect();
		player.capability(Server.class).joined(Network.SERVER, TIMEOUT);
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
		messages.notReceived(message -> message.contains(text), checkpoint, ABSENCE);
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
				checkpoint = messages.received(message -> message.contains(token), checkpoint, Duration.ofSeconds(1)).getCheckpoint();
				return;
			} catch (IllegalStateException notYet) {
				// The client did not take it yet; send it again.
			}
		}

		throw new AssertionError(name + " joined but did not take messages within " + READINESS_ATTEMPTS + " seconds");
	}

	private String received(String expected, Predicate<String> matcher) {
		ReceivedMessage message;
		try {
			message = messages.received(matcher, checkpoint, TIMEOUT);
		} catch (IllegalStateException failure) {
			throw new AssertionError(name + " did not receive " + expected + "; since its previous step it received "
					+ messages.history().subList(checkpoint, messages.history().size()), failure);
		}
		checkpoint = message.getCheckpoint();

		return message.getText();
	}
}
