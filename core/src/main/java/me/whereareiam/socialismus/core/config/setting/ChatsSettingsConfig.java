package me.whereareiam.socialismus.core.config.setting;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public class ChatsSettingsConfig {
	public boolean enabled = true;

	@Comment(
			value = {
					@CommentValue(" Allows to disable the vanilla chat usage on Paper and its forks."),
			},
			at = Comment.At.PREPEND
	)
	public boolean useVanillaChat = true;
}
