package me.whereareiam.socialismus.core.config.module.bubblechat;

import me.whereareiam.socialismus.api.type.BubbleTriggerType;
import me.whereareiam.socialismus.core.integration.protocollib.entity.metadata.display.type.AlignmentType;
import me.whereareiam.socialismus.core.integration.protocollib.entity.metadata.display.type.DisplayType;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import org.joml.Vector3f;

public class BubbleChatSettingsConfig {

	@Comment(
			value = {
					@CommentValue(type = CommentValue.Type.NEW_LINE),
					@CommentValue(" Trigger type specifies the way the bubble message is triggered."),
					@CommentValue(" Available values: CHAT, COMMAND, CHAT_COMMAND"),
			},
			at = Comment.At.PREPEND
	)
	public BubbleTriggerType triggerType = BubbleTriggerType.CHAT;

	@Comment(
			value = {
					@CommentValue(type = CommentValue.Type.NEW_LINE),
					@CommentValue(" Alignment type specifies to which border side the message will be floated."),
					@CommentValue(" Available values: LEFT, RIGHT and CENTER."),
			},
			at = Comment.At.PREPEND
	)
	public AlignmentType alignmentType = AlignmentType.CENTER;

	@Comment(
			value = {
					@CommentValue(" Display type is the way the bubble with the message follows the player's gaze."),
					@CommentValue(" Available values: FIXED, VERTICAL, HORIZONTAL and CENTER."),
			},
			at = Comment.At.PREPEND
	)
	public DisplayType displayType = DisplayType.VERTICAL;

	@Comment(
			value = {
					@CommentValue("  Enable swapper allows you to enable or disable the swapper."),
			},
			at = Comment.At.PREPEND
	)
	public boolean enableSwapper = true;

	@Comment(
			value = {
					@CommentValue(" The See Through option controls how the bubble will behave behind a structure."),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue(" Available values:"),
					@CommentValue(" true - you will see the bubble behind a block (Armor stand behaviour)"),
					@CommentValue(" false - false - you will not see the bubble behind a block."),
			},
			at = Comment.At.PREPEND
	)
	public boolean seeThrough = false;

	@Comment(
			value = {
					@CommentValue(" The line width options allow you to specify the width of a single line."),
					@CommentValue(" 100 corresponds to 16 symbols per line."),
			},
			at = Comment.At.PREPEND
	)
	public int lineWidth = 100;

	@Comment(
			value = {
					@CommentValue(" Line Count allows you to limit the number of lines to display."),
					@CommentValue(" You can write a number from 1 to infinite here"),
			},
			at = Comment.At.PREPEND
	)
	public int lineCount = 5;

	@Comment(
			value = {
					@CommentValue(" This setting represents the distance between a bubble message and the player's head."),
					@CommentValue(" You can set a value from 0 to infinite."),
			},
			at = Comment.At.PREPEND
	)
	public int headDistance = 1;


	@Comment(
			value = {
					@CommentValue(" This setting allows you to adjust how much time is given to read a single symbol, resulting"),
					@CommentValue(" in the main display time being calculated for the entire bubble message."),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue(" Example:"),
					@CommentValue("  \"I love bananas!\" - has 13 symbols, so we multiply 13 by 0.1 and we get 1.3 seconds"),
					@CommentValue("  to display this message.")
			},
			at = Comment.At.PREPEND
	)
	public double timePerSymbol = 0.1;

	@Comment(
			value = {
					@CommentValue(" This setting controls a part of the behaviour of the previous one. Here you can set the"),
					@CommentValue(" minimum time a player has to read a message. For example, if we have calculated 1.3"),
					@CommentValue(" seconds, it would not be used, instead the 2 seconds from this option would be used."),
			},
			at = Comment.At.PREPEND
	)
	public double minimumTime = 1.5;

	@Comment(
			value = {
					@CommentValue(" Display scale allows you to adjust the scale of the bubble."),
			},
			at = Comment.At.PREPEND
	)
	public Vector3f displayScale = new Vector3f((float) 1.0, (float) 1.0, (float) 1.0);

	@Comment(
			value = {
					@CommentValue(" This setting allows you to set the maximum number of mentions in a single message."),
			},
			at = Comment.At.PREPEND
	)
	public int maxMentions = 5;

	@Comment(
			value = {
					@CommentValue(" The sound option allows you to specify the sound that will be played when a bubble message is displayed."),
			},
			at = Comment.At.PREPEND
	)
	public BubbleChatSoundConfig sound = new BubbleChatSoundConfig();
}
