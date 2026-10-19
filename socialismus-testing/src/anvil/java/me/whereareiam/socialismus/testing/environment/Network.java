package me.whereareiam.socialismus.testing.environment;

import me.whereareiam.anvil.api.model.process.Distribution;
import me.whereareiam.anvil.api.model.process.MinecraftProxy;
import me.whereareiam.anvil.api.model.process.MinecraftServer;
import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.anvil.api.type.Platforms;
import me.whereareiam.anvil.api.type.ProcessLifetime;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * Where a journey runs Socialismus: on a server players join directly, on a proxy in front of a backend, or on
 * several processes of a network of a proxy and two backends. Every network runs the newest Minecraft version Anvil's
 * players speak.
 */
public enum Network {
	/** A Paper server with the PAPER jar. */
	PAPER(List.of(Network.SERVER)),
	/** A Spigot server with the BUKKIT jar. */
	SPIGOT(List.of(Network.SERVER)),
	/** A Velocity proxy with the VELOCITY jar, in front of a Paper backend without Socialismus. */
	VELOCITY(List.of(Network.PROXY)),
	/** A Velocity proxy without Socialismus in front of two Paper backends, lobby and survival, that both run it. */
	BACKENDS(List.of(Network.LOBBY, Network.SURVIVAL)),
	/** A Velocity proxy and its two Paper backends, lobby and survival, that all run Socialismus. */
	PROXY_AND_BACKENDS(List.of(Network.PROXY, Network.LOBBY, Network.SURVIVAL)),
	/** A Velocity proxy and its lobby backend run Socialismus; its survival backend runs none. */
	PROXY_AND_LOBBY(List.of(Network.PROXY, Network.LOBBY));

	/** The server players join directly; on the Velocity network, the backend behind the proxy. */
	public static final String SERVER = "server";
	/** The proxy of every network with one. */
	public static final String PROXY = "proxy";
	/** The backend players join first on the networks of two backends. */
	public static final String LOBBY = "lobby";
	/** The other backend of the networks of two backends. */
	public static final String SURVIVAL = "survival";

	private static final String MINECRAFT_VERSION = "26.1.2";

	private final List<String> socialismus;

	Network(List<String> socialismus) {
		this.socialismus = socialismus;
	}

	/**
	 * Returns the processes that run Socialismus.
	 */
	@NotNull
	public List<String> socialismus() {
		return socialismus;
	}

	/**
	 * Returns the process that runs Socialismus; on a network where several do, the first of them.
	 */
	@NotNull
	public String process() {
		return socialismus.getFirst();
	}

	/**
	 * Returns the server players stand on once they joined.
	 */
	@NotNull
	public String entryServer() {
		return backends().getFirst();
	}

	/**
	 * Returns whether the network has a proxy players join through.
	 */
	public boolean proxied() {
		return this != PAPER && this != SPIGOT;
	}

	/**
	 * Returns Socialismus's data directory of the process that runs it first in a running network.
	 *
	 * @param anvil the running network
	 */
	@NotNull
	public Path dataDirectory(@NotNull ScenarioContext anvil) {
		return dataDirectory(anvil, process());
	}

	/**
	 * Returns Socialismus's data directory of a process in a running network.
	 *
	 * @param anvil   the running network
	 * @param process a process that runs Socialismus
	 */
	@NotNull
	public Path dataDirectory(@NotNull ScenarioContext anvil, @NotNull String process) {
		return anvil.processes().get(process).workDirectory().resolve(data(process));
	}

	/**
	 * Returns the network a running scenario was built as, which its name begins with.
	 *
	 * @param anvil the running network
	 */
	@NotNull
	public static Network of(@NotNull ScenarioContext anvil) {
		String name = anvil.definition().getName();
		return valueOf(name.split("-")[1].toUpperCase(Locale.ROOT));
	}

	/**
	 * Returns the backends of the network: the server players join directly, or those behind the proxy.
	 */
	@NotNull
	List<String> backends() {
		return switch (this) {
			case PAPER, SPIGOT, VELOCITY -> List.of(SERVER);
			case BACKENDS, PROXY_AND_BACKENDS, PROXY_AND_LOBBY -> List.of(LOBBY, SURVIVAL);
		};
	}

	/**
	 * Returns the Anvil artifact of the jar a process of this network installs.
	 *
	 * @param process a process that runs Socialismus
	 * @param bundle  whether the network installs the bundle jar instead of its platform's jar
	 */
	@NotNull
	String artifact(@NotNull String process, boolean bundle) {
		if (bundle && this == SPIGOT)
			throw new IllegalArgumentException("The bundle jar does not carry the Spigot platform");
		if (bundle) return "bundle";
		if (process.equals(PROXY)) return "velocity";

		return this == SPIGOT ? "bukkit" : "paper";
	}

	/**
	 * Returns Socialismus's data directory, relative to the workspace of a process that runs it.
	 */
	@NotNull
	String data(@NotNull String process) {
		return process.equals(PROXY) ? "plugins/socialismus" : "plugins/Socialismus";
	}

	@NotNull
	MinecraftServer.MinecraftServerBuilder server(@NotNull String name) {
		MinecraftServer.MinecraftServerBuilder server = MinecraftServer.builder()
				.name(name)
				.memoryMegabytes(1024)
				.setting("view-distance", "4")
				.setting("simulation-distance", "4");

		if (this == SPIGOT)
			return server.platform(Platforms.SPIGOT)
					.distribution(Distribution.pinned(MINECRAFT_VERSION, "95f871fd6d055ba10b5a058768ddad43b0be0286480c8eba435c835c95d5f19c"));

		server.platform(Platforms.PAPER).distribution(Distribution.remote(MINECRAFT_VERSION, "74"));
		// Behind the proxy the backend runs no Socialismus, so it serves one network after another.
		if (this == VELOCITY)
			server.lifetime(ProcessLifetime.ENGINE);

		return server;
	}

	@NotNull
	MinecraftProxy.MinecraftProxyBuilder proxy() {
		MinecraftProxy.MinecraftProxyBuilder proxy = MinecraftProxy.builder()
				.name(PROXY)
				.platform(Platforms.VELOCITY)
				.distribution(Distribution.remote("3.5.1", "615"))
				.setting("advanced.login-ratelimit", "0")
				// Journeys chat and run commands at once, which Velocity's command rate limit would drop.
				.setting("advanced.command-rate-limit", "0")
				.setting("advanced.forward-commands-if-rate-limited", "false")
				.defaultServer(entryServer());
		backends().forEach(proxy::server);

		return proxy;
	}
}
