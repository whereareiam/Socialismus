package me.whereareiam.socialismus.common.updater.provider;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.input.updater.UpdateProvider;
import me.whereareiam.socialismus.api.model.module.UpdateSpecification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Singleton
public class SpigotMCProvider implements UpdateProvider {
	private static final String UPDATE_URL = "https://api.spigotmc.org/legacy/update.php?resource=";

	@Override
	public Optional<String> fetchLatest(UpdateSpecification.Spec spec) throws IOException {
		URL url = new URL(UPDATE_URL + spec.getId());
		try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
			String version = in.readLine();
			return Optional.ofNullable(version);
		}
	}

	@Override
	public List<String> fetchRecentUpdates(UpdateSpecification.Spec spec, int limit) throws IOException {
		return fetchLatest(spec)
				.map(List::of)
				.orElse(List.of());
	}
}
