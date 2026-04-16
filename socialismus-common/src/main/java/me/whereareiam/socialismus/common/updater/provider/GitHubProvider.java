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
public class GitHubProvider implements UpdateProvider {
    private static final ConfigReader JSON_READER = Config.reader(Format.JSON);

    @Override
    public Optional<String> fetchLatest(UpdateSource source) throws IOException {
        String api = "https://api.github.com/repos/" + source.getId() + "/releases/latest";

        try (InputStream in = request(api)) {
            return decodeLatest(in);
        }
    }

    @Override
    public List<String> fetchRecentUpdates(UpdateSource source, int limit) throws IOException {
        String api = "https://api.github.com/repos/" + source.getId() + "/commits?per_page=" + limit;

        try (InputStream in = request(api)) {
            return decodeRecentUpdates(in);
        }
    }

    Optional<String> decodeLatest(InputStream in) {
        GitHubRelease release = JSON_READER.decode(in, GitHubRelease.class);
        return Optional.ofNullable(release.tag_name);
    }

    List<String> decodeRecentUpdates(InputStream in) {
        GitHubCommit[] commits = JSON_READER.decode(in, GitHubCommit[].class);
        return Arrays.stream(commits)
                .map(c -> c.sha)
                .collect(Collectors.toList());
    }

    InputStream request(String urlString) throws IOException {
        return UpdateHttpClient.get(urlString, "application/vnd.github+json");
    }

    @Data
    static class GitHubRelease {
        private String tag_name;
    }

    @Data
    static class GitHubCommit {
        private String sha;
    }
}
