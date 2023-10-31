package me.whereareiam.socialismus.config.setting;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.setting.database.DatabaseSettingsConfig;
import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.language.object.YamlSerializable;

@Singleton
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
                    @CommentValue(" This option allows you to disable or enable the update checker, which checks"),
                    @CommentValue(" for updates every hour."),
            },
            at = Comment.At.PREPEND
    )
    public boolean updateChecker = true;

    public DatabaseSettingsConfig database = new DatabaseSettingsConfig();

    @Comment(
            value = {
                    @CommentValue(type = CommentValue.Type.NEW_LINE),
                    @CommentValue(" In this section you can disable or enable the desired features."),
                    @CommentValue("  true - enabled"),
                    @CommentValue("  false - disabled"),
            },
            at = Comment.At.PREPEND
    )
    public FeaturesSettingsConfig features = new FeaturesSettingsConfig();

    @Comment(
            value = {
                    @CommentValue(type = CommentValue.Type.NEW_LINE),
                    @CommentValue(" In this section, you can switch the plugin's behavior mode, which"),
                    @CommentValue(" can impact its performance and usage of resources."),
                    @CommentValue("  true - enabled"),
                    @CommentValue("  false - disabled"),
            },
            at = Comment.At.PREPEND
    )
    public PerformanceSettingsConfig performance = new PerformanceSettingsConfig();

    public SettingsConfig() {
        super(SettingsConfig.SETTINGS);
    }
}