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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class ModrinthProvider implements UpdateProvider {
	private static final ConfigReader JSON_READER = Config.reader(Format.JSON);

	@Override
	public Optional<String> fetchLatest(UpdateSource source) throws IOException {
		try (InputStream in = requestVersions(source, 1)) {
			return decodeVersions(in).stream()
					.findFirst()
					.map(ModrinthVersion::getVersion_number);
		}
	}

	@Override
	public List<String> fetchRecentUpdates(UpdateSource source, int limit) throws IOException {
		try (InputStream in = requestVersions(source, limit)) {
			return decodeVersions(in).stream()
					.map(v -> v.version_number)
					.collect(Collectors.toList());
		}
	}

	InputStream requestVersions(UpdateSource source, int limit) throws IOException {
		return UpdateHttpClient.get("https://api.modrinth.com/v2/project/"
				+ source.getId()
				+ "/version?limit=" + limit, "application/json");
	}

	List<ModrinthVersion> decodeVersions(InputStream in) throws IOException {
		ModrinthVersion[] versions = JSON_READER.decode(in, ModrinthVersion[].class);
		return versions == null ? List.of() : Arrays.asList(versions);
	}

	@Data
	static class ModrinthVersion {
		private String version_number;
	}
}
