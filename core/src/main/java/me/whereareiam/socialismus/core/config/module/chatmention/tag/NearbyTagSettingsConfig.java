package me.whereareiam.socialismus.core.config.module.chatmention.tag;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

import java.util.List;

public class NearbyTagSettingsConfig {
	@Comment(
			value = {
					@CommentValue(type = CommentValue.Type.NEW_LINE),
					@CommentValue(" Here you can specify the permission required to use the tag."),
			},
			at = Comment.At.PREPEND
	)
	public String permission = "socialismus.chat.mention.nearby";

	@Comment(
			value = {
					@CommentValue(type = CommentValue.Type.NEW_LINE),
					@CommentValue(" Here you can specify the tags that will be used to mention nearby players."),
			},
			at = Comment.At.PREPEND
	)
	public List<String> tags = List.of("@nearby", "@near");

	@Comment(
			value = {
					@CommentValue(type = CommentValue.Type.NEW_LINE),
					@CommentValue(" Here you can specify the format that will be used to tag nearby players."),
					@CommentValue(" You can use PlaceholderAPI placeholders, MiniMessage formatting and internal"),
					@CommentValue(" {usedTag} placeholder.")
			},
			at = Comment.At.PREPEND
	)
	public String format = "<yellow>{usedTag}</yellow>";
}
