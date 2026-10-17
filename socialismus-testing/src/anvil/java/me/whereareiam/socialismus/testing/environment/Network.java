package me.whereareiam.socialismus.testing.environment;

import me.whereareiam.anvil.api.model.process.Distribution;
import me.whereareiam.anvil.api.model.process.MinecraftProxy;
import me.whereareiam.anvil.api.model.process.MinecraftServer;
import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.anvil.api.type.Platforms;
import me.whereareiam.anvil.api.type.ProcessLifetime;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * Where a journey runs Socialismus: on a server players join directly, or on a proxy in front of a backend.
 * Every network runs the newest Minecraft version Anvil's players speak.
 */
public enum Network {
	/** A Paper server with the PAPER jar. */
	PAPER("paper", "plugins/Socialismus"),
	/** A Spigot server with the BUKKIT jar. */
	SPIGOT("bukkit", "plugins/Socialismus"),
	/** A Velocity proxy with the VELOCITY jar, in front of a Paper backend without Socialismus. */
	VELOCITY("velocity", "plugins/socialismus");

	/** The server players join directly; on the Velocity network, the backend behind the proxy. */
	public static final String SERVER = "server";
	/** The proxy of the Velocity network. */
	public static final String PROXY = "proxy";

	private static final String MINECRAFT_VERSION = "26.1.2";

	private final String artifact;
	private final String data;

	Network(String artifact, String data) {
		this.artifact = artifact;
		this.data = data;
	}

	/**
	 * Returns the Anvil artifact of the jar this network installs.
	 *
	 * @param bundle whether the network installs the bundle jar instead of its platform's jar
	 */
	@NotNull
	String artifact(boolean bundle) {
		if (bundle && this == SPIGOT)
			throw new IllegalArgumentException("The bundle jar does not carry the Spigot platform");

		return bundle ? "bundle" : artifact;
	}

	/**
	 * Returns Socialismus's data directory, relative to the workspace of the process that runs it.
	 */
	@NotNull
	String data() {
		return data;
	}

	/**
	 * Returns the name of the process that runs Socialismus.
	 */
	@NotNull
	public String process() {
		return this == VELOCITY ? PROXY : SERVER;
	}

	/**
	 * Returns Socialismus's data directory in a running network.
	 *
	 * @param anvil the running network
	 */
	@NotNull
	public Path dataDirectory(@NotNull ScenarioContext anvil) {
		Path workspace = this == VELOCITY
				? anvil.processes().proxy(PROXY).workDirectory()
				: anvil.processes().server(SERVER).workDirectory();

		return workspace.resolve(data);
	}

	/**
	 * Returns the network a running scenario was built as.
	 *
	 * @param anvil the running network
	 */
	@NotNull
	public static Network of(@NotNull ScenarioContext anvil) {
		if (!anvil.definition().getProxies().isEmpty()) return VELOCITY;

		String platform = anvil.definition().getServers().getFirst().getPlatform();
		return Platforms.SPIGOT.equals(platform) ? SPIGOT : PAPER;
	}

	@NotNull
	MinecraftServer.MinecraftServerBuilder server() {
		MinecraftServer.MinecraftServerBuilder server = MinecraftServer.builder()
				.name(SERVER)
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
		return MinecraftProxy.builder()
				.name(PROXY)
				.platform(Platforms.VELOCITY)
				.distribution(Distribution.remote("3.5.1", "615"))
				.setting("advanced.login-ratelimit", "0")
				// Journeys chat and run commands at once, which Velocity's command rate limit would drop.
				.setting("advanced.command-rate-limit", "0")
				.setting("advanced.forward-commands-if-rate-limited", "false")
				.server(SERVER)
				.defaultServer(SERVER);
	}
}
