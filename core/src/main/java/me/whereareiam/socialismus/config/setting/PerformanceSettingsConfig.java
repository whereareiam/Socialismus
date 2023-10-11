package me.whereareiam.socialismus.config.setting;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public class PerformanceSettingsConfig {
    @Comment(
            value = {
                    @CommentValue(" Saves each player in RAM and unloads when the player leaves. "),
                    @CommentValue(" Consumes more RAM, but the CPU usage will be lower with a large online count."),
            },
            at = Comment.At.PREPEND
    )
    public boolean playerCache = true;
}
