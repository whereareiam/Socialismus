package me.whereareiam.socialismus.config.module.bubblechat;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public class BubbleChatRequirementConfig {

    @Comment(
            value = {
                    @CommentValue(type = CommentValue.Type.NEW_LINE),
                    @CommentValue(" Here you can specify a permission that is required to see a bubble message."),
                    @CommentValue(" Required for sender or recipient. Set \"\" to disable.")
            },
            at = Comment.At.PREPEND
    )
    public String seePermission = "privilege.1";

    @Comment(
            value = {
                    @CommentValue(" Here you can specify whether or not a player can send a bubble message."),
                    @CommentValue(" Set \"\" to disable.")
            },
            at = Comment.At.PREPEND
    )
    public String sendPermission = "privilege.0";

    @Comment(
            value = {
                    @CommentValue(" This requirement allows you to specify how many symbols are required to"),
                    @CommentValue(" display a bubble message. If the condition is not met, the bubble message"),
                    @CommentValue(" will not be displayed.")
            },
            at = Comment.At.PREPEND
    )
    public int symbolCountThreshold = 5;
}
