package me.whereareiam.socialismus.testing.sync;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.Network;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Console;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SocialismusNetwork on a server without a transport: it joins the network alone, under the node id its first start
 * wrote, and offers the network to the other modules.
 */
@Tag("sync")
class NetworkModuleTest {
	private static final Pattern JOINED = Pattern.compile("Joining the network as (server-[0-9a-f]{8})");

	@Test
	@Socialismus(modules = Module.NETWORK)
	void joinsTheNetworkAloneUnderTheNodeIdItWrote(ScenarioContext anvil) throws IOException {
		String log = Console.of(anvil).log();

		assertTrue(log.contains("Loaded module SocialismusNetwork"), "SocialismusNetwork did not load:\n" + log);
		assertTrue(log.contains("Registered resource socialismusnetwork:network from module SocialismusNetwork"),
				"The network was not registered:\n" + log);
		Matcher joined = JOINED.matcher(log);
		assertTrue(joined.find(), "SocialismusNetwork did not say which node it is:\n" + log);
		assertFalse(log.contains("Replicating through"), "SocialismusNetwork found a transport that is not there:\n" + log);

		Path settings = Network.of(anvil).dataDirectory(anvil).resolve("modules/SocialismusNetwork/settings.yml");
		String written = Files.readString(settings);
		assertTrue(written.contains(joined.group(1)), "The node id is not kept in the settings:\n" + written);
	}
}
