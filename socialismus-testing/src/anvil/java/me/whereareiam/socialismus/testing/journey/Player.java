package me.whereareiam.socialismus.testing.journey;

import me.whereareiam.anvil.api.model.player.PlayerLogin;
import me.whereareiam.anvil.api.model.player.PlayerOptions;
import me.whereareiam.anvil.api.model.player.PlayerProfile;
import me.whereareiam.anvil.api.player.SimulatedPlayer;
import me.whereareiam.anvil.api.process.ProcessConsole;
import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.anvil.capability.entities.Entities;
import me.whereareiam.anvil.capability.entities.EntityQuery;
import me.whereareiam.anvil.capability.entities.EntityWatch;
import me.whereareiam.anvil.capability.entities.model.SeenEntity;
import me.whereareiam.anvil.capability.messages.MessageWatch;
import me.whereareiam.anvil.capability.messages.Messages;
import me.whereareiam.anvil.capability.messages.model.ChatLines;
import me.whereareiam.anvil.capability.players.PlayerWatch;
import me.whereareiam.anvil.capability.players.Players;
import me.whereareiam.anvil.capability.screen.ActionBarWatch;
import me.whereareiam.anvil.capability.screen.BossBarWatch;
import me.whereareiam.anvil.capability.screen.Screen;
import me.whereareiam.anvil.capability.screen.TitleWatch;
import me.whereareiam.anvil.capability.screen.model.BossBar;
import me.whereareiam.anvil.capability.screen.model.Title;
import me.whereareiam.anvil.capability.session.Session;
import me.whereareiam.anvil.capability.sounds.SoundWatch;
import me.whereareiam.anvil.capability.sounds.Sounds;
import me.whereareiam.anvil.capability.sounds.model.HeardSound;
import me.whereareiam.socialismus.testing.environment.Network;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
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

	/** How far beside the player it floats over an entity may spawn and still count as above it. */
	private static final double ABOVE = 1.5;

	private final ScenarioContext anvil;
	private final String name;
	private final SimulatedPlayer player;
	private final Messages messages;
	/** The player's messages after the one its previous expectation found, where its next expectation looks. */
	private final MessageWatch inbox;
	/** The titles, action bars, boss bars and sounds after the ones the player's previous expectations found. */
	private final TitleWatch titles;
	private final ActionBarWatch actionBars;
	private final BossBarWatch bossBars;
	private final SoundWatch sounds;
	private final Entities entities;
	/** The entities that appeared after the one its previous expectation found, where its next expectation looks. */
	private final EntityWatch sights;

	private Player(ScenarioContext anvil, String name, SimulatedPlayer player) {
		this.anvil = anvil;
		this.name = name;
		this.player = player;
		this.messages = player.capability(Messages.class);
		this.inbox = messages.watch();
		Screen screen = player.capability(Screen.class);
		this.titles = screen.watchTitles();
		this.actionBars = screen.watchActionBars();
		this.bossBars = screen.watchBossBars();
		this.sounds = player.capability(Sounds.class).watch();
		this.entities = player.capability(Entities.class);
		this.sights = entities.watch();
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
	 * Expects a title whose title or subtitle contains the given text.
	 *
	 * @return the title, with its subtitle and display times
	 */
	@NotNull
	public Title seesTitle(@NotNull String text) {
		try {
			return titles.shown(text, TIMEOUT);
		} catch (IllegalStateException failure) {
			throw new AssertionError(name + " was not shown the title \"" + text + "\"; since its previous title it was "
					+ "shown " + titles.unread(), failure);
		}
	}

	/**
	 * Expects an action bar that contains the given text.
	 *
	 * @return the whole action bar
	 */
	@NotNull
	public String seesActionBar(@NotNull String text) {
		try {
			return actionBars.shown(text, TIMEOUT);
		} catch (IllegalStateException failure) {
			throw new AssertionError(name + " was not shown the action bar \"" + text + "\"; since its previous action "
					+ "bar it was shown " + actionBars.unread(), failure);
		}
	}

	/**
	 * Expects a boss bar whose text contains the given text to appear or change.
	 *
	 * @return the bar after that change
	 */
	@NotNull
	public BossBar seesBossBar(@NotNull String text) {
		try {
			return bossBars.shown(text, TIMEOUT);
		} catch (IllegalStateException failure) {
			throw new AssertionError(name + " was not shown the boss bar \"" + text + "\"; since its previous boss bar "
					+ "it saw " + bossBars.unread(), failure);
		}
	}

	/**
	 * Expects a boss bar whose text contains the given text to leave the screen.
	 *
	 * @return the bar as it was last shown
	 */
	@NotNull
	public BossBar seesBossBarRemoved(@NotNull String text) {
		try {
			return bossBars.removed(text, TIMEOUT);
		} catch (IllegalStateException failure) {
			throw new AssertionError("The boss bar \"" + text + "\" did not leave " + name + "'s screen; since its "
					+ "previous boss bar it saw " + bossBars.unread(), failure);
		}
	}

	/**
	 * Expects a sound with the given key, such as {@code entity.player.levelup}; a key without a namespace means the
	 * {@code minecraft} namespace.
	 *
	 * @return the sound, with its volume and pitch
	 */
	@NotNull
	public HeardSound hears(@NotNull String key) {
		try {
			return sounds.heard(key, TIMEOUT);
		} catch (IllegalStateException failure) {
			throw new AssertionError(name + " did not hear " + key + "; since its previous sound it heard "
					+ sounds.unread(), failure);
		}
	}

	/**
	 * Returns the lines of chat the player could still scroll back to now, modelled on the vanilla client's chat. A
	 * line missing from them is gone from every client's chat; a line they hold may already be gone on a narrow one.
	 */
	@NotNull
	public ChatLines chatLines() {
		return messages.chatLines();
	}

	/**
	 * Expects an entity above another player whose name or text shows the given text, such as a chat bubble: one
	 * that rides that player, or one that spawns right above it and rides the entity below it.
	 *
	 * @param speaker the player the entity floats over
	 * @return the entity as it appeared
	 */
	@NotNull
	public SeenEntity seesAbove(@NotNull Player speaker, @NotNull String text) {
		SeenEntity body = sees(speaker);
		EntityQuery above = EntityQuery.matching("above " + speaker.name(), entity ->
				Objects.equals(entity.getVehicleId(), body.getId())
						|| Math.hypot(entity.getX() - body.getX(), entity.getZ() - body.getZ()) <= ABOVE);
		EntityQuery query = EntityQuery.labelled(text).and(above);

		try {
			return sights.appeared(query, TIMEOUT);
		} catch (IllegalStateException failure) {
			throw new AssertionError(name + " did not see an entity " + query + "; since its previous step it saw "
					+ sights.unread(), failure);
		}
	}

	/**
	 * Expects that no entity whose name or text shows the given text appears for a few seconds.
	 */
	@NotNull
	public Player neverSeesLabelled(@NotNull String text) {
		sights.neverAppeared(EntityQuery.labelled(text), ABSENCE);
		return this;
	}

	/**
	 * Expects that an entity the player saw disappears.
	 *
	 * @param entity the entity, as an earlier step returned it
	 */
	@NotNull
	public Player seesVanish(@NotNull SeenEntity entity) {
		try {
			sights.disappeared(entity, TIMEOUT);
		} catch (IllegalStateException failure) {
			throw new AssertionError(name + " still sees " + entity.getType() + " '" + entity.label() + "'", failure);
		}

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

	@NotNull
	public String name() {
		return name;
	}

	/**
	 * Waits until the player sees another player's own entity, by the profile the server granted that player.
	 */
	private SeenEntity sees(Player other) {
		PlayerProfile profile = Objects.requireNonNull(other.player.grantedProfile(), other.name() + " is not online");
		UUID uniqueId = profile.getUniqueId();

		return entities.present(EntityQuery.ofType("player").and(entity -> uniqueId.equals(entity.getUniqueId())),
				TIMEOUT);
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
