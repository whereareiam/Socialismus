package me.whereareiam.socialismus.testing.essentials;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Essentials' {@code /socialismus features}, which lists the features that run; Essentials has none at the moment.
 */
@Tag("essentials")
class FeaturesCommandTest {
	private static final String MESSAGES = "modules/Essentials/messages.yml";

	@Test
	@Socialismus(modules = Module.ESSENTIALS, configure = KnownTexts.class)
	void featuresSaysThatNoFeatureRuns(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();

		alice.command("socialismus features");

		alice.seesAll("Features:", "no features running");
	}

	static final class KnownTexts implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.put(MESSAGES, Map.of("commands", Map.of("featuresCommand", Map.of(
					"format", List.of("Features:", "{features}"),
					"featureFormat", "running: {feature}",
					"noFeatures", "no features running"))));
		}
	}
}
