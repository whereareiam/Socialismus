package me.whereareiam.socialismus.common.updater.provider;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.update.UpdateSource;
import me.whereareiam.socialismus.service.UpdateProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

@Singleton
public class SpigotMCProvider implements UpdateProvider {
	private static final String UPDATE_URL = "https://api.spigotmc.org/legacy/update.php?resource=";

	@Override
	public Optional<String> fetchLatest(UpdateSource source) throws IOException {
		try (InputStream in = request(source)) {
			return decodeLatest(in);
		}
	}

	@Override
	public List<String> fetchRecentUpdates(UpdateSource source, int limit) throws IOException {
		return fetchLatest(source)
				.map(List::of)
				.orElse(List.of());
	}

	InputStream request(UpdateSource source) throws IOException {
		return UpdateHttpClient.get(UPDATE_URL + source.getId(), "text/plain");
	}

	Optional<String> decodeLatest(InputStream in) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			return Optional.ofNullable(reader.readLine());
		}
	}
}
