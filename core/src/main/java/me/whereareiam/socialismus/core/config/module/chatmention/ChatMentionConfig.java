package me.whereareiam.socialismus.core.config.module.chatmention;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.chatmention.ChatMentionFormat;
import me.whereareiam.socialismus.api.model.chatmention.ChatMentionNotificationFormat;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.language.object.YamlSerializable;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class ChatMentionConfig extends YamlSerializable {
	@Comment(
			value = {
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue(" CHAT MENTION DOCUMENTATION"),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue(" What is Chat Mention?"),
					@CommentValue(" It is a module that allows your players to mention other players in chat."),
					@CommentValue(" It is very simple and easy to use, just type '@' and the name of the player you want to"),
					@CommentValue(" mention and the module will take care of the rest."),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue(" Here you can create as many formats as you like."),
					@CommentValue(" formats: "),
					@CommentValue("  Here you can specify if the format is enabled or not"),
					@CommentValue("  - enabled: true"),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue("    Here you can specify permission to use this format, if the player does not have"),
					@CommentValue("    the required permission, the format will not be used."),
					@CommentValue("    *You can leave it empty to disable permission check*"),
					@CommentValue("    permission: \"privilege.3\""),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue("    Format that will be used when the player mentions another player."),
					@CommentValue("    Don't forget to close the tags, otherwise the text will not be displayed correctly."),
					@CommentValue("    *You can use PlaceholderAPI placeholders and MiniMessage formatting*"),
					@CommentValue("    format: \"<yellow><bold>@{playerName}</bold></yellow>\""),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue("    Here you can specify how the hover when you put the mouse over the mentioned player"),
					@CommentValue("    hover-format: "),
					@CommentValue("      - \"Player {playerName} was mentioned!\""),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue(" Here you can specify how the mentioned player will be notified."),
					@CommentValue(" notifications:"),
					@CommentValue("   Here you can specify if the notification is enabled or not"),
					@CommentValue("   - enabled: true"),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue("     Here you can specify permission to use this notification, if the player does not have"),
					@CommentValue("     the required permission, the notification will not be used."),
					@CommentValue("     *You can leave it empty to disable permission check*"),
					@CommentValue("     permission: \"privilege.3\""),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue("     Here you can specify the types of notifications that will be used."),
					@CommentValue("     Available types:"),
					@CommentValue("       - ACTION_BAR"),
					@CommentValue("       - BOSS_BAR"),
					@CommentValue("       - CHAT"),
					@CommentValue("       - SOUND"),
					@CommentValue("       - TITLE"),
					@CommentValue("     You can specify the order in which the notifications will be sent."),
					@CommentValue("     types:"),
					@CommentValue("       - TITLE"),
					@CommentValue("       - SOUND"),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue("     action-bar:"),
					@CommentValue("       Here you can specify the message that will be displayed in the action bar."),
					@CommentValue("       message: \"{mentionerName} mentioned you!\""),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue("     boss-bar:"),
					@CommentValue("       Here you can specify the message that will be displayed in the boss bar."),
					@CommentValue("       message: \"{mentionerName} mentioned you!\""),
					@CommentValue("       Here you can specify the color. Available colors:"),
					@CommentValue("         - BLUE, GREEN, PINK, PURPLE, RED, WHITE, YELLOW"),
					@CommentValue("       color: RED"),
					@CommentValue("       Here you can specify the style. Available styles:"),
					@CommentValue("         - SOLID, SEGMENTED_6, SEGMENTED_10, SEGMENTED_12, SEGMENTED_20"),
					@CommentValue("       style: SOLID"),
					@CommentValue("       Here you can specify the duration how long the boss bar will be displayed."),
					@CommentValue("       duration: 30"),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue("     chat:"),
					@CommentValue("       Here you can specify the message that will be displayed in the chat."),
					@CommentValue("       message: \"{mentionerName} mentioned you!\""),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue("     sound:"),
					@CommentValue("       Here you can specify the sound that will be played."),
					@CommentValue("       sound: ENTITY_EXPERIENCE_ORB_PICKUP"),
					@CommentValue("       Here you can specify the volume of the sound."),
					@CommentValue("       volume: 1.0"),
					@CommentValue("       Here you can specify the pitch of the sound."),
					@CommentValue("       pitch: 1.0"),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue("     title:"),
					@CommentValue("       Here you can specify the title that will be displayed."),
					@CommentValue("       title: \"{mentionerName} mentioned you!\""),
					@CommentValue("       Here you can specify the subtitle that will be displayed."),
					@CommentValue("       subtitle: \"\""),
					@CommentValue("       Here you can specify the fade in time in milliseconds."),
					@CommentValue("       fadeIn: 10"),
					@CommentValue("       Here you can specify the stay time in milliseconds."),
					@CommentValue("       stay: 30"),
					@CommentValue("       Here you can specify the fade out time in milliseconds."),
					@CommentValue("       fadeOut: 10"),
					@CommentValue(type = CommentValue.Type.NEW_LINE),
			}
	)
	public List<ChatMentionFormat> formats = new ArrayList<>();
	public List<ChatMentionNotificationFormat> notifications = new ArrayList<>();
	public ChatMentionSettingsConfig settings = new ChatMentionSettingsConfig();
}
