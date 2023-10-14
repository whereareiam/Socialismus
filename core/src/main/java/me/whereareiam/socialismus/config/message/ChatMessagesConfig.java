package me.whereareiam.socialismus.config.message;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public class ChatMessagesConfig {
    @Comment(
            value = {
                    @CommentValue(" Set 'noOnlinePlayer: null' to disable the message"),
            },
            at = Comment.At.PREPEND
    )
    public String noOnlinePlayers = "<gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>Nobody received your message because there are no players on the server.";
    @Comment(
            value = {
                    @CommentValue(" Set 'noNearbyPlayers: null' to disable the message"),
            },
            at = Comment.At.PREPEND
    )
    public String noNearbyPlayers = "<gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>No one has heard you.";
    public String lackOfRequirements = "<gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>You <red>do not have the necessary requirements <white>to write in this chat.";
}
