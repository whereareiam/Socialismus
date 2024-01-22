package me.whereareiam.socialismus.core.config.module.chatmention;

import me.whereareiam.socialismus.core.config.module.chatmention.tag.AllTagSettingsConfig;
import me.whereareiam.socialismus.core.config.module.chatmention.tag.NearbyTagSettingsConfig;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public class ChatMentionSettingsConfig {
	public String selfMentionPermission = "socialismus.chat.mention.self";

	@Comment(
			value = {
					@CommentValue(type = CommentValue.Type.NEW_LINE),
					@CommentValue(" Here you can specify a permission that will limit the amount of mentions a player can do."),
					@CommentValue(" Example: socialismus.chat.mention.max.3 will limit the player to 3 mentions per message."),
			},
			at = Comment.At.PREPEND
	)
	public String maxMentionPermission = "socialismus.chat.mention.max.";
	public AllTagSettingsConfig allTagSettings = new AllTagSettingsConfig();
	public NearbyTagSettingsConfig nearbyTagSettings = new NearbyTagSettingsConfig();
}
