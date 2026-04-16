package me.whereareiam.socialismus.common.updater.provider;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

final class UpdateHttpClient {
	private static final int TIMEOUT_MS = 8_000;
	private static final String USER_AGENT = "Socialismus-Updater";

	private UpdateHttpClient() {
	}

	static InputStream get(String urlString, String accept) throws IOException {
		URL url = URI.create(urlString).toURL();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("GET");
		conn.setConnectTimeout(TIMEOUT_MS);
		conn.setReadTimeout(TIMEOUT_MS);
		conn.setRequestProperty("User-Agent", USER_AGENT);
		if (accept != null && !accept.isBlank()) {
			conn.setRequestProperty("Accept", accept);
		}

		return conn.getInputStream();
	}
}
