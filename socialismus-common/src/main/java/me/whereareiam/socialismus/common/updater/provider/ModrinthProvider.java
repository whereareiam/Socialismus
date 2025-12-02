package me.whereareiam.socialismus.common.updater.provider;

import com.google.inject.Singleton;
import lombok.Data;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.reader.ConfigReader;
import me.whereareiam.configura.type.Format;
import me.whereareiam.socialismus.model.update.UpdateSource;
import me.whereareiam.socialismus.service.UpdateProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class ModrinthProvider implements UpdateProvider {
	private static final ConfigReader JSON_READER = Config.reader(Format.JSON);

	@Override
	public Optional<String> fetchLatest(UpdateSource source) throws IOException {
		URL url = URI.create("https://api.modrinth.com/v2/project/"
				+ source.getId() + "/version").toURL();

		try (InputStream in = url.openStream()) {
			ModrinthVersionList versionList = JSON_READER.decode(in, ModrinthVersionList.class);
			return versionList.versions.isEmpty()
					? Optional.empty()
					: Optional.ofNullable(versionList.versions.get(0).version_number);
		}
	}

	@Override
	public List<String> fetchRecentUpdates(UpdateSource source, int limit) throws IOException {
		URL url = URI.create("https://api.modrinth.com/v2/project/"
				+ source.getId()
				+ "/version?limit=" + limit).toURL();

		try (InputStream in = url.openStream()) {
			ModrinthVersionList versionList = JSON_READER.decode(in, ModrinthVersionList.class);
			return versionList.versions.stream()
					.map(v -> v.version_number)
					.collect(Collectors.toList());
		}
	}

	@Data
	private static class ModrinthVersionList {
		private List<ModrinthVersion> versions = new ArrayList<>();
	}

	@Data
	private static class ModrinthVersion {
		private String version_number;
	}
}
