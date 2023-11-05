package me.whereareiam.socialismus.config.setting.database;

import me.whereareiam.socialismus.database.DatabaseType;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public class DatabaseSettingsConfig {
    @Comment(
            value = {
                    @CommentValue(" This option allows you to select the database type."),
                    @CommentValue(" Available types: SQLITE"),
            },
            at = Comment.At.PREPEND
    )
    public DatabaseType databaseType = DatabaseType.SQLITE;

    @Comment(
            value = {
                    @CommentValue(type = CommentValue.Type.NEW_LINE),
                    @CommentValue(" In this section you can configure your SQLite database."),
            },
            at = Comment.At.PREPEND
    )
    public SQLiteDatabaseConfig sqlite = new SQLiteDatabaseConfig();
}
