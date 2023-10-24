package me.whereareiam.socialismus;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Singleton
public class Updater {
    private final Plugin plugin;
    private final LoggerUtil loggerUtil;

    @Inject
    public Updater(Plugin plugin, LoggerUtil loggerUtil, SettingsConfig settingsConfig,
                   Scheduler scheduler) {
        this.plugin = plugin;
        this.loggerUtil = loggerUtil;

        if (settingsConfig.updateChecker)
            scheduler.scheduleAtFixedRate(this::checkForUpdates, 0, 1, TimeUnit.HOURS, Optional.empty());

        loggerUtil.trace("Initializing class: " + this);
    }

    private void checkForUpdates() {
        String latestVersion = getLatestVersion();
        final String currentVersion = plugin.getDescription().getVersion();

        if (!currentVersion.equals(latestVersion)) {
            loggerUtil.warning("Update found! The latest version is " + latestVersion);
            loggerUtil.warning("Download here: https://www.spigotmc.org/resources/113119/updates");
        }
    }

    private String getLatestVersion() {
        BufferedReader reader = null;
        HttpURLConnection conn = null;
        try {
            String updateUrl = "https://api.spigotmc.org/legacy/update.php?resource=113119";
            URL url = new URL(updateUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            return reader.readLine();
        } catch (Exception e) {
            loggerUtil.severe(e.getMessage());
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    loggerUtil.severe("Failed to close reader: " + e.getMessage());
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
