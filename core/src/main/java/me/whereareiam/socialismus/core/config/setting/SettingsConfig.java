package me.whereareiam.socialismus.core.config.setting;

import com.google.inject.Singleton;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.language.object.YamlSerializable;

@Singleton
public class SettingsConfig extends YamlSerializable {

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

    /*@Comment(
            value = {
                    @CommentValue(type = CommentValue.Type.NEW_LINE),
                    @CommentValue(" In this section you will be able to configure your database."),
            },
            at = Comment.At.PREPEND
    )
    public DatabaseSettingsConfig database = new DatabaseSettingsConfig();*/

	@Comment(
			value = {
					@CommentValue(type = CommentValue.Type.NEW_LINE),
					@CommentValue(" In this section you can disable or enable the desired module."),
					@CommentValue("  true - enabled"),
					@CommentValue("  false - disabled"),
			},
			at = Comment.At.PREPEND
	)
	public ModulesSettingsConfig modules = new ModulesSettingsConfig();

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
}