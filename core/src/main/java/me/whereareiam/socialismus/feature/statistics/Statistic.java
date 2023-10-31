package me.whereareiam.socialismus.feature.statistics;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public abstract class Statistic<T> {
    protected final LoggerUtil loggerUtil;
    protected final Map<String, T> statistics = new ConcurrentHashMap<>();
    protected final String url;

    public Statistic(LoggerUtil loggerUtil, Plugin plugin, String tableName) {
        this.loggerUtil = loggerUtil;

        String dbPath = plugin.getDataFolder().toPath().resolve("features").resolve("statistics").resolve("stats.db").toString();
        this.url = "jdbc:sqlite:" + dbPath;

        loadStatistics();
    }

    public void loadStatistics() {
        String sql = "SELECT chat_id, count FROM statistics";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                try {
                    statistics.put(rs.getString("chat_id"), parseValue(rs.getString("count")));
                } catch (NumberFormatException e) {
                    loggerUtil.severe("Error parsing count for chat " + rs.getString("chat_id") + ": " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            loggerUtil.severe("Error loading statistics: " + e.getMessage());
        }
    }

    public void saveStatistics() {
        String sql = "INSERT INTO statistics(chat_id,count) VALUES(?,?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Map.Entry<String, T> entry : statistics.entrySet()) {
                pstmt.setString(1, entry.getKey());
                pstmt.setString(2, formatValue(entry.getValue()));
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            loggerUtil.severe("Error saving statistics: " + e.getMessage());
        }
    }

    protected abstract T parseValue(String value);

    protected abstract String formatValue(T value);
}
