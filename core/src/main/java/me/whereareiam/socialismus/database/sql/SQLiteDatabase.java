package me.whereareiam.socialismus.database.sql;

import com.google.inject.Inject;
import me.whereareiam.socialismus.database.Database;
import me.whereareiam.socialismus.util.LoggerUtil;

import java.sql.*;

public class SQLiteDatabase implements Database {
    private final LoggerUtil loggerUtil;
    private Connection connection;
    private String url;

    @Inject
    public SQLiteDatabase(LoggerUtil loggerUtil) {
        this.loggerUtil = loggerUtil;
    }

    @Override
    public boolean connect() {
        try {
            connection = DriverManager.getConnection(url);
            return true;
        } catch (SQLException e) {
            loggerUtil.severe(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                return true;
            }
        } catch (SQLException e) {
            loggerUtil.severe(e.getMessage());
        }
        return false;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public ResultSet executeQuery(String sql) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            return statement.executeQuery();
        } catch (SQLException e) {
            loggerUtil.severe(e.getMessage());
            return null;
        }
    }

    @Override
    public int executeUpdate(String sql) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            return statement.executeUpdate();
        } catch (SQLException e) {
            loggerUtil.severe(e.getMessage());
            return -1;
        }
    }
}