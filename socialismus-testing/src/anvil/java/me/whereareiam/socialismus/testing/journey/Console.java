package me.whereareiam.socialismus.testing.journey;

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

/**
 * The console of the process that runs Socialismus: runs commands as the console and reads what the process
 * printed. Output printed after a command is watched from the moment it is sent; output from before the test,
 * such as the startup banner, is read from the process's log.
 */
public final class Console {
	private static final Duration TIMEOUT = Duration.ofSeconds(15);

	private final ScenarioContext anvil;
	private final String process;
	private final ProcessConsole console;

	private Console(ScenarioContext anvil, String process, ProcessConsole console) {
		this.anvil = anvil;
		this.process = process;
		this.console = console;
	}

	/**
	 * Returns the console of the process that runs Socialismus.
	 */
	@NotNull
	public static Console of(@NotNull ScenarioContext anvil) {
		Network network = Network.of(anvil);
		String process = network.process();
		ProcessConsole console = network == Network.VELOCITY
				? anvil.processes().proxy(process).console()
				: anvil.processes().server(process).console();

		return new Console(anvil, process, console);
	}

	/**
	 * Runs a command as the console.
	 *
	 * @return what the process prints from now on
	 */
	@NotNull
	public Output run(@NotNull String command) {
		long after = console.checkpoint();
		console.sendCommand(command);

		return text -> console.await(text, after, TIMEOUT);
	}

	/**
	 * Returns what the process printed since it started, from its log.
	 */
	@NotNull
	public String log() {
		Path workspace = Network.of(anvil) == Network.VELOCITY
				? anvil.processes().proxy(process).workDirectory()
				: anvil.processes().server(process).workDirectory();
		Path log = workspace.resolve("logs/latest.log");
		try {
			return Files.readString(log, StandardCharsets.UTF_8);
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
