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
    public String noOnlinePlayers = "Nobody received your message because there are no players on the server.";
    @Comment(
            value = {
                    @CommentValue(" Set 'noNearbyPlayers: null' to disable the message"),
            },
            at = Comment.At.PREPEND
    )
    public String noNearbyPlayers = "No one has heard you.";
    public String lackOfRequirements = "You do not have the necessary requirements to write in this chat.";
}
