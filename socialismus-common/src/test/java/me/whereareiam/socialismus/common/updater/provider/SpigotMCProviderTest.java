package me.whereareiam.socialismus.common.updater.provider;

import me.whereareiam.socialismus.type.module.ProviderType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpigotMCProviderTest extends AbstractUpdateProviderTest<SpigotMCProvider> {
	@Override
	protected SpigotMCProvider createProvider() {
		return new SpigotMCProvider();
	}

	@Test
	void parsesSingleLineVersionResponse() throws IOException {
		assertEquals(Optional.of("2.0.0-RC6"), provider.decodeLatest(stream("2.0.0-RC6\n")));
	}

	@Test
	@EnabledIfSystemProperty(named = "socialismus.liveApiTests", matches = "true")
	void liveApiReturnsParsableVersion() throws IOException {
		assertLiveLatestIsPresent(source(ProviderType.SPIGOT, "113119"));
	}
}
