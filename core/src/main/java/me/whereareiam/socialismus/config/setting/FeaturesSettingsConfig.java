package me.whereareiam.socialismus.config.setting;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public class FeaturesSettingsConfig {
    @Comment(
            value = {
                    @CommentValue(type = CommentValue.Type.NEW_LINE),
                    @CommentValue(" The chat feature changes the behaviour of the vanilla Minecraft chat and adds"),
                    @CommentValue(" more formatting and customization, allowing you to create several different"),
                    @CommentValue(" chats with different requirements and styles."),
            },
            at = Comment.At.PREPEND
    )
    public boolean chats = true;

    @Comment(
            value = {
                    @CommentValue(" Swapper feature adds to your chat message shortcuts, emojis or anything you"),
                    @CommentValue(" can imagine."),
            },
            at = Comment.At.PREPEND
    )
    public SwapperSettingsConfig swapper = new SwapperSettingsConfig();

    @Comment(
            value = {
                    @CommentValue(type = CommentValue.Type.NEW_LINE),
                    @CommentValue(" Bubble chat is a feature that changes the way players chat with each other."),
                    @CommentValue(" It adds a bubble (hologram) with a message from the chat over the player's head."),
                    @CommentValue(" *Works only if ProtocolLib is installed*"),
            },
            at = Comment.At.PREPEND
    )
    public boolean bubblechat = true;
}
