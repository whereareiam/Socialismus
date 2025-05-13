package me.whereareiam.socialismus.common;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.AnsiColor;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.model.scheduler.PeriodicalRunnableTask;
import me.whereareiam.socialismus.api.output.Scheduler;
import me.whereareiam.socialismus.shared.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

@Singleton
public class Updater {
	private final Settings.Updater updater;
	private final Scheduler scheduler;

	private final Gson gson = new Gson();

	@Inject
	public Updater(
			Settings settings,
			Scheduler scheduler
	) {
		this.updater = settings.getUpdater();
		this.scheduler = scheduler;
	}

	public void start() {
		if (!updater.isCheckForUpdates()) return;
		if (updater.getInterval() <= 0) return;

		scheduler.schedule(
				PeriodicalRunnableTask.builder()
						.period((long) updater.getInterval() * 60 * 60 * 1000)
						.runnable(this::checkForUpdate)
						.module("main")
						.delay(0)
						.build(),
				true
		);
	}

	private void checkForUpdate() {
		String version = Constants.VERSION;
		if (updater.isWarnAboutDevBuilds() && version.equals("DEV")) {
			Logger.info("You are using a " + AnsiColor.ORANGE + "local build" + AnsiColor.RESET + " of Socialismus.");
			return;
		}

		if (updater.isWarnAboutDevBuilds() && version.startsWith("DEV-")) {
			checkDevBuild(version);
			return;
		}

		if (updater.isWarnAboutUpdates()) {
			checkNonDevBuild(version);
		}
	}

	private void checkDevBuild(String version) {
		String currentCommit = version.split("-")[1].toLowerCase();
		String response = request("https://api.github.com/repos/whereareiam/Socialismus/commits");

		JsonArray jsonArray = gson.fromJson(response, JsonArray.class);
		int commitCount = 0;

		for (JsonElement jsonElement : jsonArray) {
			String commit = jsonElement.getAsJsonObject().get("sha").getAsString();
			if (commit.startsWith(currentCommit)) {
				break;
			}
			commitCount++;
		}

		if (commitCount == 0) {
			Logger.info("You are using a dev build of the plugin.");
		} else {
			Logger.warn("You are using a dev build of the plugin, but you are behind by " + commitCount + " commits.");
		}
	}

	private void checkNonDevBuild(String version) {
		String response = request("https://api.modrinth.com/v2/project/Socialismus/version");

		JsonArray jsonArray = gson.fromJson(response, JsonArray.class);
		String latestVersion = jsonArray.get(0).getAsJsonObject().get("version_number").getAsString();

		if (version.matches("\\d+\\.\\d+\\.\\d+")) {
			String[] latestVersionParts = latestVersion.split("\\.");
			String[] versionParts = version.split("\\.");

			int versionDifference = 0;
			for (int i = 0; i < 3; i++) {
				versionDifference += Integer.parseInt(latestVersionParts[i]) - Integer.parseInt(versionParts[i]);
			}

			if (versionDifference == 0) {
				Logger.info("Your version is up to date.");
			} else {
				Logger.warn("Your version is behind by " + versionDifference + " versions [Current: " + version + ", Latest: " + latestVersion + "]");
				Logger.warn("Download the latest version at https://modrinth.com/plugin/socialismus/versions");
			}
		}
	}

	private String request(String urlString) {
		try {
			URL url = URI.create(urlString).toURL();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String line;
			StringBuilder result = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}

			reader.close();

			return result.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}