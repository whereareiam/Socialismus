package me.whereareiam.socialismus.common.updater.provider;

import me.whereareiam.socialismus.type.module.ProviderType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GitHubProviderTest extends AbstractUpdateProviderTest<GitHubProvider> {
	@Override
	protected GitHubProvider createProvider() {
		return new GitHubProvider();
	}

	@Test
	void parsesLatestReleaseAndRecentCommitArrays() throws IOException {
		assertEquals(Optional.of("v2.0.0"), provider.decodeLatest(stream("""
				{"tag_name":"v2.0.0"}
				""")));
		assertEquals(
				List.of("abc123", "def456"),
				provider.decodeRecentUpdates(stream("""
						[
						  {"sha":"abc123"},
						  {"sha":"def456"}
						]
						"""))
		);
	}

	@Test
	@EnabledIfSystemProperty(named = "socialismus.liveApiTests", matches = "true")
	void liveApiReturnsParsableReleaseAndCommits() throws IOException {
		var source = source(ProviderType.GITHUB, "whereareiam/Socialismus");

		assertLiveLatestIsPresent(source);
		assertLiveRecentUpdatesArePresent(source, 2);
	}
}
