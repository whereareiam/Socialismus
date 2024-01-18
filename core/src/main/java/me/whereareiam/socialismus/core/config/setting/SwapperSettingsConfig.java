package me.whereareiam.socialismus.core.config.setting;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public class SwapperSettingsConfig {
	public boolean enabled = true;
	@Comment(
			value = {
					@CommentValue(" Suggests swappers in chat (only for swappers without regex)."),
					@CommentValue(" *Works only if ProtocolLib is installed*"),
			},
			at = Comment.At.PREPEND
	)
	public boolean suggest = true;
}
