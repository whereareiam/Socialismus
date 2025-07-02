package me.whereareiam.socialismus.common.updater.provider;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.input.updater.UpdateProvider;
import me.whereareiam.socialismus.api.model.module.UpdateSpecification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Singleton
public class GitHubProvider implements UpdateProvider {
	private final Gson gson = new Gson();
	private static final int DEFAULT_UPDATE_LIMIT = 30;

	@Override
	public Optional<String> fetchLatest(UpdateSpecification spec) throws IOException {
		String api = "https://api.github.com/repos/" + spec.getId() + "/releases/latest";
		String json = request(api);
		JsonObject obj = gson.fromJson(json, JsonObject.class);
        
		return Optional.ofNullable(obj.get("tag_name"))
				.map(JsonElement::getAsString);
	}

	@Override
	public List<String> fetchRecentUpdates(UpdateSpecification spec, int limit) throws IOException {
		String api = "https://api.github.com/repos/" + spec.getId() + "/commits?per_page=" + limit;
		String json = request(api);
		JsonArray arr = gson.fromJson(json, JsonArray.class);
		return StreamSupport.stream(arr.spliterator(), false)
				.map(e -> e.getAsJsonObject()
						.get("sha")
						.getAsString())
				.collect(Collectors.toList());
	}

	private String request(String urlString) throws IOException {
		URL url = URI.create(urlString).toURL();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(8_000);
		conn.setReadTimeout(8_000);
		try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
			return in.lines().collect(Collectors.joining());
		}
	}
}
