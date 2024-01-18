package me.whereareiam.socialismus.core.config.setting.database;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

import java.util.List;

public class SQLiteDatabaseConfig {
	@Comment(
			value = {
					@CommentValue(" Here you can name your database folder."),
			},
			at = Comment.At.PREPEND
	)
	public String storageDir = ".storage";

	@Comment(
			value = {
					@CommentValue(type = CommentValue.Type.NEW_LINE),
					@CommentValue(" In this list you can specify different names of databases that will be stored"),
					@CommentValue(" in the above folder, but it is not recommended to change these names."),
			},
			at = Comment.At.PREPEND
	)
	public List<String> files = List.of("core", "statistic");
}
