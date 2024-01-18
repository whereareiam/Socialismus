package me.whereareiam.socialismus.core.config.message;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public class ChatMessagesConfig {
	@Comment(
			value = {
					@CommentValue(" Set 'no-online-players: null' to disable the message"),
			},
			at = Comment.At.PREPEND
	)
	public String noOnlinePlayers = "<gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>Nobody received your message because there are no players on the server.";
	@Comment(
			value = {
					@CommentValue(" Set 'no-nearby-players: null' to disable the message"),
			},
			at = Comment.At.PREPEND
	)
	public String noNearbyPlayers = " <gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>No one has heard you.";
	public String insufficientPlayers = " <gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>There must be at least {minOnline} players on the server to send a message to this chat";
	public String noUsePermission = " <gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>You've no permission to <red>send<white> a message";
	public String tooSmallMessage = " <gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>Your message is too short to be sent.";
	public String forbiddenWorld = " <gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>You can't chat in this world.";
}
