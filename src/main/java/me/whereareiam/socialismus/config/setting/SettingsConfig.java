package me.whereareiam.socialismus.config.setting;

import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.language.object.YamlSerializable;

public class SettingsConfig extends YamlSerializable {
    private static final SerializerConfig SETTINGS = new SerializerConfig.Builder().build();
    @Comment(
            value = {
                    @CommentValue(" 0 - Standard information"),
                    @CommentValue(" 1 - Debug mode"),
                    @CommentValue(" 2 - Debug mode but with much more information"),
            },
            at = Comment.At.PREPEND
    )
    public int logLevel = 0;

    @Comment(
            value = {
                    @CommentValue(type = CommentValue.Type.NEW_LINE),
                    @CommentValue(" In this section you can disable or enable the desired features."),
                    @CommentValue("  true - enabled"),
                    @CommentValue("  false - disabled"),
            },
            at = Comment.At.PREPEND
    )
    public FeaturesConfig featuresConfig = new FeaturesConfig();

    public SettingsConfig() {
        super(SettingsConfig.SETTINGS);
    }
}