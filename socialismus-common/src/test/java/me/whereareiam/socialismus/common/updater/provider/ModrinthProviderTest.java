package me.whereareiam.socialismus.common.updater.provider;

import me.whereareiam.socialismus.type.module.ProviderType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModrinthProviderTest extends AbstractUpdateProviderTest<ModrinthProvider> {
	@Override
	protected ModrinthProvider createProvider() {
		return new ModrinthProvider();
	}

	@Test
	void parsesVersionArraysForLatestAndRecentUpdates() throws IOException {
		String payload = """
				[
				  {"version_number":"2.0.0-RC6"},
				  {"version_number":"2.0.0-RC5"}
				]
				""";

		assertEquals(
				List.of("2.0.0-RC6", "2.0.0-RC5"),
				provider.decodeVersions(stream(payload)).stream()
						.map(ModrinthProvider.ModrinthVersion::getVersion_number)
						.toList()
		);
	}

	@Test
	void returnsEmptyWhenNoVersionsExist() throws IOException {
		assertEquals(List.of(), provider.decodeVersions(stream("[]")));
	}

	@Test
	@EnabledIfSystemProperty(named = "socialismus.liveApiTests", matches = "true")
	void liveApiReturnsParsableVersions() throws IOException {
		var source = source(ProviderType.MODRINTH, "socialismus");

		assertLiveLatestIsPresent(source);
		assertLiveRecentUpdatesArePresent(source, 2);
	}
}
