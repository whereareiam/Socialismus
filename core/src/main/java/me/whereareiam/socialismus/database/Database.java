package me.whereareiam.socialismus.database;

import java.sql.ResultSet;

public interface Database {
    boolean connect();

    boolean disconnect();

    void setUrl(String url);

    ResultSet executeQuery(String sql);

    int executeUpdate(String sql);
}
