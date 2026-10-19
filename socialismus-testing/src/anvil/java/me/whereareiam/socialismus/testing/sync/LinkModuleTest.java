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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SocialismusLink on a server without a proxy that listens: it registers its transport, writes a secret, and keeps
 * trying to reach the proxy, saying so once.
 */
@Tag("sync")
class LinkModuleTest {
	@Test
	@Socialismus(modules = {Module.NETWORK, Module.LINK})
	void keepsTryingToReachTheProxyAndSaysSoOnce(ScenarioContext anvil) throws IOException, InterruptedException {
		Thread.sleep(5000);
		String log = Console.of(anvil).log();

		assertTrue(log.contains("Registered resource socialismusnetwork:transport from module SocialismusLink"),
				"SocialismusLink did not register its transport:\n" + log);
		assertEquals(1, count(log, "Not connected to the link at"), "SocialismusLink did not warn exactly once:\n" + log);

		Path settings = Network.of(anvil).dataDirectory(anvil).resolve("modules/SocialismusLink/settings.yml");
		assertTrue(Files.readString(settings).matches("(?s).*secret: \"[0-9a-f]{48}\".*"), "No secret was written:\n" + Files.readString(settings));
	}

	private static int count(String text, String part) {
		int count = 0;
		for (int index = text.indexOf(part); index >= 0; index = text.indexOf(part, index + 1)) count++;
		return count;
	}
}
