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
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class GitHubProvider implements UpdateProvider {
	private static final ConfigReader JSON_READER = Config.reader(Format.JSON);

	@Override
	public Optional<String> fetchLatest(UpdateSource source) throws IOException {
		String api = "https://api.github.com/repos/" + source.getId() + "/releases/latest";

		try (InputStream in = request(api)) {
			GitHubRelease release = JSON_READER.decode(in, GitHubRelease.class);
			return Optional.ofNullable(release.tag_name);
		}
	}

	@Override
	public List<String> fetchRecentUpdates(UpdateSource source, int limit) throws IOException {
		String api = "https://api.github.com/repos/" + source.getId() + "/commits?per_page=" + limit;

		try (InputStream in = request(api)) {
			GitHubCommitList commitList = JSON_READER.decode(in, GitHubCommitList.class);
			return commitList.commits.stream()
					.map(c -> c.sha)
					.collect(Collectors.toList());
		}
	}

	private InputStream request(String urlString) throws IOException {
		URL url = URI.create(urlString).toURL();

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(8_000);
		conn.setReadTimeout(8_000);

		return conn.getInputStream();
	}

	@Data
	private static class GitHubRelease {
		private String tag_name;
	}

	@Data
	private static class GitHubCommitList {
		private List<GitHubCommit> commits = new ArrayList<>();
	}

	@Data
	private static class GitHubCommit {
		private String sha;
	}
}

