package me.whereareiam.socialismus.config;

import me.whereareiam.socialismus.util.message.FormattingType;
import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.language.object.YamlSerializable;

public class SettingsConfig extends YamlSerializable {
    private static final SerializerConfig SETTINGS = new SerializerConfig.Builder().build();

    public SettingsConfig() {
        super(SettingsConfig.SETTINGS);
    }

    @Comment(
            value = {
                    @CommentValue(" Select a text formatting method, available:"),
                    @CommentValue("  CUSTOM - Custom text formatting that works on all platforms."),
                    @CommentValue("  MINIMESSAGE - Text formatting from Mini Message, works on Paper and its forks.")
            },
            at = Comment.At.PREPEND
    )
    public String formattingType = FormattingType.CUSTOM.toString();
    @Comment(
            value = {
                    @CommentValue(" If enabled, it prints additional information to the console."),
            },
            at = Comment.At.PREPEND
    )
    public boolean debug;
}