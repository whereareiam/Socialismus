package me.whereareiam.socialismus.core.config.module.bubblechat;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public class BubbleChatFormatConfig {

	@Comment(
			value = {
					@CommentValue(type = CommentValue.Type.NEW_LINE),
					@CommentValue(" The Start Message Format allows you to customise how the start message is displayed."),
					@CommentValue(" You can use PlaceholderAPI placeholders, MiniMessage formatting and internal"),
					@CommentValue(" {playerName}/{message} placeholders.")
			},
			at = Comment.At.PREPEND
	)
	public String startMessageFormat = "<gold><bold>\uD83D\uDD14 <reset><white>{message}";

	@Comment(
			value = {
					@CommentValue(" This option is similar to the previous one, but is used for starting messages in the queue."),
					@CommentValue(" You can use PlaceholderAPI placeholders, MiniMessage formatting and internal"),
					@CommentValue(" {playerName}/{message} placeholders.")
			},
			at = Comment.At.PREPEND
	)
	public String queueMessageFormat = "<white>...{message}";

	@Comment(
			value = {
					@CommentValue(" If a message doesn't fit into a single bubble, it will be cut off and printed in the next"),
					@CommentValue(" bubble. Here you can adjust how the cut is displayed."),
					@CommentValue(" You can use PlaceholderAPI placeholders and MiniMessage formatting.")
			},
			at = Comment.At.PREPEND
	)
	public String cutMessageFormat = "<white>...";

	@Comment(
			value = {
					@CommentValue(" At the end of a single bubble or queue, you can add a message to the end."),
					@CommentValue(" You can use PlaceholderAPI placeholders and MiniMessage formatting.")
			},
			at = Comment.At.PREPEND
	)
	public String endMessageFormat = "<white>.";

	@Comment(
			value = {
					@CommentValue(" This option allows you to specify the color of the background. Doesn't work"),
					@CommentValue(" if opacity is set to 0. You must use the RGB format."),
			},
			at = Comment.At.PREPEND
	)
	public String backgroundColor = "#000000";

	@Comment(
			value = {
					@CommentValue(" Adjust the opacity of your background colour or turn off the background."),
					@CommentValue(" Values from 0 to 255 are available. (Set 0 to disable)"),
			},
			at = Comment.At.PREPEND
	)
	public int backgroundOpacity = 40;

	@Comment(
			value = {
					@CommentValue(" This option allows you to add a beautiful shadow to your text."),
					@CommentValue(" If set to true, it will be displayed."),
			},
			at = Comment.At.PREPEND
	)
	public boolean textShadow = false;
}
