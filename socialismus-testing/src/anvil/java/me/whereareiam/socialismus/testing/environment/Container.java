package me.whereareiam.socialismus.testing.environment;

import me.whereareiam.anvil.api.model.process.ContainerProcess;
import org.jetbrains.annotations.NotNull;

/**
 * A container Anvil runs as a process of a journey's network, such as the Redis that SocialismusRedis replicates
 * through. It runs on Docker while the servers run on the host, and Socialismus's files reach it through Anvil's
 * {@code ${anvil.process.<name>.host}} and {@code ${anvil.process.<name>.port}}.
 */
public enum Container {
	REDIS(ContainerProcess.builder()
			.name("redis")
			.image("redis:7.2-alpine")
			.port(6379)
			.readyLog("Ready to accept connections")
			.executionProviderId("docker")
			.build());

	private final ContainerProcess process;

	Container(ContainerProcess process) {
		this.process = process;
	}

	/**
	 * Returns the container's process name, which Anvil's placeholders and {@code anvil.processes()} use.
	 */
	@NotNull
	public String processName() {
		return process.getName();
	}

	@NotNull
	ContainerProcess process() {
		return process;
	}
}
