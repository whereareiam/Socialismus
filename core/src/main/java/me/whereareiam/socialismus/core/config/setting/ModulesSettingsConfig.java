package me.whereareiam.socialismus.core.config.setting;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public class ModulesSettingsConfig {
	@Comment(
			value = {
					@CommentValue(type = CommentValue.Type.NEW_LINE),
					@CommentValue(" The chat module changes the behaviour of the vanilla Minecraft chat and adds"),
					@CommentValue(" more formatting and customization, allowing you to create several different"),
					@CommentValue(" chat with different requirements and styles."),
			},
			at = Comment.At.PREPEND
	)
	public boolean chats = true;

	@Comment(
			value = {
					@CommentValue(" Swapper module adds to your chat message shortcuts, emojis or anything you"),
					@CommentValue(" can imagine."),
			},
			at = Comment.At.PREPEND
	)
	public SwapperSettingsConfig swapper = new SwapperSettingsConfig();

	@Comment(
			value = {
					@CommentValue(type = CommentValue.Type.NEW_LINE),
					@CommentValue(" Bubble chat is a module that changes the way players chat with each other."),
					@CommentValue(" It adds a bubble (hologram) with a message from the chat over the player's head."),
					@CommentValue(" *Works only if ProtocolLib is installed*"),
			},
			at = Comment.At.PREPEND
	)
	public boolean bubblechat = true;

	@Comment(
			value = {
					@CommentValue(type = CommentValue.Type.NEW_LINE),
					@CommentValue(" The announcer module allows you to create announcements that will be displayed"),
					@CommentValue(" on the server. You can create as many announcements as you want and set the"),
					@CommentValue(" time interval between them."),
			},
			at = Comment.At.PREPEND
	)
	public boolean announcer = true;

	@Comment(
			value = {
					@CommentValue(type = CommentValue.Type.NEW_LINE),
					@CommentValue(" The chatmention module allows you to mention players in chat messages."),
					@CommentValue(" When you mention a player, he will receive a notification"),
			},
			at = Comment.At.PREPEND
	)
	public boolean chatmention = true;

	/*public StatisticSettingsConfig statistic = new StatisticSettingsConfig();*/
}
