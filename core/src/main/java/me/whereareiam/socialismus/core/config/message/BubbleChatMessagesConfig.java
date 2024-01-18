package me.whereareiam.socialismus.core.config.message;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public class BubbleChatMessagesConfig {
	@Comment(
			value = {
					@CommentValue(" Set 'noSendPermission: null' to disable the message"),
			},
			at = Comment.At.PREPEND
	)
	public String noUsePermission = " <gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>You've no permission to <red>send<white> a bubble message";
	public String tooSmallMessage = " <gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>Your message is too short to be sent.";
	public String forbiddenWorld = " <gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>You can't bubble in this world.";
}
