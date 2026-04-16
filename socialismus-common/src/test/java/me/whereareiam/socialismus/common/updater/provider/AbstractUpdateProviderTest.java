package me.whereareiam.socialismus.common.updater.provider;

import me.whereareiam.socialismus.model.update.UpdateSource;
import me.whereareiam.socialismus.service.UpdateProvider;
import me.whereareiam.socialismus.type.module.ProviderType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class AbstractUpdateProviderTest<T extends UpdateProvider> {
	protected final T provider = createProvider();

	protected abstract T createProvider();

	protected static InputStream stream(String content) {
		return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
	}

	protected static UpdateSource source(ProviderType provider, String id) {
		return UpdateSource.builder()
				.provider(provider)
				.id(id)
				.build();
	}

	protected void assertLiveLatestIsPresent(UpdateSource source) throws IOException {
		assertTrue(provider.fetchLatest(source).filter(version -> !version.isBlank()).isPresent());
	}

	protected void assertLiveRecentUpdatesArePresent(UpdateSource source, int limit) throws IOException {
		List<String> recent = provider.fetchRecentUpdates(source, limit);
		assertFalse(recent.isEmpty());
		assertTrue(recent.stream().allMatch(update -> !update.isBlank()));
	}
}
