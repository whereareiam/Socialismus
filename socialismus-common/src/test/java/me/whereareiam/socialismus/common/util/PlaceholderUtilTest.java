package me.whereareiam.socialismus.common.util;

import me.whereareiam.socialismus.integration.placeholder.PlaceholderIntegration;
import me.whereareiam.socialismus.type.PlaceholderResolutionMode;
import me.whereareiam.socialismus.util.PlaceholderUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Placeholder Util Tests")
class PlaceholderUtilTest {
	private static final UUID PLAYER_ID = UUID.randomUUID();
	private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%[^%]+%");

	@Test
	@DisplayName("Should resolve placeholders once for single-pass integrations")
	void resolveUsesSinglePassForSinglePassIntegrations() {
		PlaceholderIntegration integration = new TestPlaceholderIntegration(
				Map.of(
						"%luckperms_prefix%", "<gold>[VIP]</gold> %nexo_player%",
						"%nexo_player%", "Alex"
				),
				PlaceholderResolutionMode.SINGLE_PASS
		);

		assertEquals(
				"<gold>[VIP]</gold> %nexo_player%",
				PlaceholderUtil.resolve(integration, PLAYER_ID, "%luckperms_prefix%")
		);
	}

	@Test
	@DisplayName("Should resolve chained placeholders until no placeholder syntax remains")
	void resolveUsesChainedResolutionForChainedIntegrations() {
		PlaceholderIntegration integration = new TestPlaceholderIntegration(
				Map.of(
						"%luckperms_prefix%", "<gold>[VIP]</gold> %nexo_player%",
						"%nexo_player%", "Alex"
				),
				PlaceholderResolutionMode.CHAINED
		);

		assertEquals(
				"<gold>[VIP]</gold> Alex",
				PlaceholderUtil.resolve(integration, PLAYER_ID, "%luckperms_prefix%")
		);
	}

	@Test
	@DisplayName("Should keep resolving deep placeholder chains")
	void resolveResolvesDeepPlaceholderChains() {
		Map<String, String> replacements = new HashMap<>();
		for (int i = 0; i < 12; i++) {
			replacements.put("%step" + i + "%", "%step" + (i + 1) + "%");
		}
		replacements.put("%step12%", "done");

		PlaceholderIntegration integration = new TestPlaceholderIntegration(
				replacements,
				PlaceholderResolutionMode.CHAINED
		);

		assertEquals("done", PlaceholderUtil.resolve(integration, PLAYER_ID, "%step0%"));
	}

	@Test
	@DisplayName("Should stop when placeholder resolution enters a cycle")
	void resolveStopsOnCycles() {
		PlaceholderIntegration integration = new TestPlaceholderIntegration(
				Map.of(
						"%loop_a%", "%loop_b%",
						"%loop_b%", "%loop_a%"
				),
				PlaceholderResolutionMode.CHAINED
		);

		assertEquals("%loop_a%", PlaceholderUtil.resolve(integration, PLAYER_ID, "%loop_a%"));
	}

	private static final class TestPlaceholderIntegration implements PlaceholderIntegration {
		private final Map<String, String> replacements;
		private final PlaceholderResolutionMode mode;

		private TestPlaceholderIntegration(Map<String, String> replacements, PlaceholderResolutionMode mode) {
			this.replacements = replacements;
			this.mode = mode;
		}

		@Override
		public String getName() {
			return "test";
		}

		@Override
		public boolean isAvailable() {
			return true;
		}

		@Override
		public String resolve(UUID uniqueId, String text) {
			Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
			StringBuilder resolved = new StringBuilder();

			while (matcher.find()) {
				String placeholder = matcher.group();
				String replacement = replacements.getOrDefault(placeholder, placeholder);
				matcher.appendReplacement(resolved, Matcher.quoteReplacement(replacement));
			}

			matcher.appendTail(resolved);
			return resolved.toString();
		}

		@Override
		public PlaceholderResolutionMode resolutionMode() {
			return mode;
		}
	}
}
