package me.whereareiam.socialismus.core.config.setting;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public class ChatMentionSettingsConfig {

	@Comment(
			value = {
					@CommentValue(type = CommentValue.Type.NEW_LINE),
					@CommentValue(" Enable for chats allows you to enable or disable whether the module should work"),
					@CommentValue(" in the chat."),
			},
			at = Comment.At.PREPEND
	)
	public boolean enableForChats = true;
	
	@Comment(
			value = {
					@CommentValue(" Enable for bubbles allows you to enable or disable whether the module should work"),
					@CommentValue(" in the bubbles."),
			},
			at = Comment.At.PREPEND
	)
	public boolean enableForBubbles = true;
}
