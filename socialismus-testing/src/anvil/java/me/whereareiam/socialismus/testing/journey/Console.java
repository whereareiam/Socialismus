package me.whereareiam.socialismus.testing.journey;

import me.whereareiam.anvil.api.process.ConsoleWatch;
import me.whereareiam.anvil.api.process.ProcessConsole;
import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Network;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.regex.Pattern;

/**
 * The console of a process of the network, by default the one that runs Socialismus: runs commands as the console and reads what the process
 * printed. Output printed after a command is watched from the moment it is sent; output from before the test,
 * such as the startup banner, is read from the process's log.
 */
public final class Console {
	private static final Duration TIMEOUT = Duration.ofSeconds(15);
	private static final Pattern ANSI_CODE = Pattern.compile("\u001B\\[[;\\d]*m");

	private final ScenarioContext anvil;
	private final String process;
	private final ProcessConsole console;

	private Console(ScenarioContext anvil, String process, ProcessConsole console) {
		this.anvil = anvil;
		this.process = process;
		this.console = console;
	}

	/**
	 * Returns the console of the process that runs Socialismus; on a network where several do, the first of them.
	 */
	@NotNull
	public static Console of(@NotNull ScenarioContext anvil) {
		return of(anvil, Network.of(anvil).process());
	}

	/**
	 * Returns the console of a process of the network.
	 *
	 * @param anvil   the running network
	 * @param process the process, such as {@link Network#LOBBY}
	 */
	@NotNull
	public static Console of(@NotNull ScenarioContext anvil, @NotNull String process) {
		return new Console(anvil, process, anvil.processes().get(process).console());
	}

	/**
	 * Runs a command as the console.
	 *
	 * @return what the process prints from now on
	 */
	@NotNull
	public Output run(@NotNull String command) {
		ConsoleWatch output = console.watch();
		console.sendCommand(command);

		return text -> output.printed(text, TIMEOUT);
	}

	/**
	 * Returns what the process printed since it started, from its log, without colour codes, which Spigot keeps in
	 * its log and Paper and Velocity leave out.
	 */
	@NotNull
	public String log() {
		Path workspace = anvil.processes().get(process).workDirectory();
		Path log = workspace.resolve("logs/latest.log");
		try {
			return ANSI_CODE.matcher(Files.readString(log, StandardCharsets.UTF_8)).replaceAll("");
		} catch (IOException failure) {
			throw new UncheckedIOException("Could not read " + log, failure);
		}
	}

	/**
	 * What a process prints after a command.
	 */
	@FunctionalInterface
	public interface Output {
		/**
		 * Expects a line that contains the given text.
		 *
		 * @return the line
		 */
		@NotNull
		String printed(@NotNull String text);
	}
}
