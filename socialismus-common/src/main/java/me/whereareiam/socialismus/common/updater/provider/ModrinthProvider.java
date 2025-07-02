package me.whereareiam.socialismus.common.updater.provider;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.input.updater.UpdateProvider;
import me.whereareiam.socialismus.api.model.module.UpdateSpecification;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class ModrinthProvider implements UpdateProvider {
	private final Gson gson = new Gson();

	@Override
	public Optional<String> fetchLatest(UpdateSpecification spec) throws IOException {
		URL url = URI.create("https://api.modrinth.com/v2/project/"
				+ spec.getId() + "/version").toURL();

		try (InputStream in = url.openStream();
		     Reader r = new InputStreamReader(in)) {
			JsonArray arr = gson.fromJson(r, JsonArray.class);
			return Optional.ofNullable(arr.get(0))
					.map(e -> e.getAsJsonObject()
							.get("version_number")
							.getAsString());
		}
	}

	@Override
	public List<String> fetchRecentUpdates(UpdateSpecification spec, int limit) throws IOException {
		URL url = URI.create("https://api.modrinth.com/v2/project/"
				+ spec.getId()
				+ "/version?limit=" + limit).toURL();

		try (InputStream in = url.openStream();
		     Reader r = new InputStreamReader(in)) {

			JsonArray arr = gson.fromJson(r, JsonArray.class);
			List<String> versions = new ArrayList<>(arr.size());
			for (JsonElement e : arr) {
				versions.add(
						e.getAsJsonObject()
								.get("version_number")
								.getAsString()
				);
			}
			return versions;
		}
	}

}
