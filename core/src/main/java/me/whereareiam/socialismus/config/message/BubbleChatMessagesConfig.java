package me.whereareiam.socialismus.config.message;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public class BubbleChatMessagesConfig {
    @Comment(
            value = {
                    @CommentValue(" Set 'noSendPermission: null' to disable the message"),
            },
            at = Comment.At.PREPEND
    )
    public String noSendPermission = " <gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>You've no permission to <red>send<white> a bubble message";
}
