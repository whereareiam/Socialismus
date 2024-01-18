package me.whereareiam.socialismus.core.config.module.announcer;

import me.whereareiam.socialismus.api.model.announcement.Announcement;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.language.object.YamlSerializable;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementConfig extends YamlSerializable {

	@Comment(
			value = {
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue(" ANNOUNCEMENT DOCUMENTATION:"),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue(" Here you can create as many Announcements as you like."),
					@CommentValue(" announcements:"),
					@CommentValue("   Here you can specify an announcement id, it should be unique and not used in any other announcement."),
					@CommentValue("   - id: \"example\""),
					@CommentValue("     Here you can specify if the announcement is enabled or not."),
					@CommentValue("     enabled: true"),
					@CommentValue("     Here you can specify the message that will be sent to the players."),
					@CommentValue("     message:"),
					@CommentValue("       - \"<red>Example Announcement\""),
					@CommentValue("       - \"<green>Example Announcement\""),
					@CommentValue("     Here you can specify the settings for the announcement."),
					@CommentValue("     settings:"),
					@CommentValue("       This option allows you to specify the delay between sending messages."),
					@CommentValue("       delay: 0"),
					@CommentValue("       This option allows you to specify if the announcement will be sent repeatedly."),
					@CommentValue("       repeat: true"),
					@CommentValue("       This option allows you to specify the worlds in which the announcement will be sent."),
					@CommentValue("       worlds:"),
					@CommentValue("         - \"world\""),
					@CommentValue("         - \"world_nether\""),
					@CommentValue("     Here you can specify the requirements for sending this announcement."),
					@CommentValue("     requirements:"),
					@CommentValue("       This option allows you to disable requirements check, it is advised to think about if you"),
					@CommentValue("       need the requirements or not, because if you disable the performance will be much better."),
					@CommentValue("       enabled: true"),
					@CommentValue("       This option allows you to specify the permission that the player must have to receive this"),
					@CommentValue("       permission: \"i.love.whereareiam\""),
					@CommentValue("       This option allows you to specify the worlds in which the player can receive this announcement."),
					@CommentValue("       worlds:"),
					@CommentValue("         - \"world\""),
					@CommentValue("         - \"world_nether\""),
					@CommentValue(type = CommentValue.Type.NEW_LINE),
			},
			at = Comment.At.PREPEND
	)
	public List<Announcement> announcements = new ArrayList<>();
}
