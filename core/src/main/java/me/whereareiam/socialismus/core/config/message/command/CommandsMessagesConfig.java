package me.whereareiam.socialismus.core.config.message.command;

public class CommandsMessagesConfig {
	public String onlyForPlayer = " <gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>This command can only be used by players.";
	public String wrongSyntax = " <gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>You used the command <red>incorrectly.";
	public String unknownCommand = " <gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>You used a command that <red>doesn't <white>exist.";
	public String errorOccurred = "An error occurred while executing the command.";
	public String missingArgument = " <gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>You did not <red>specify the required argument <white>to execute the command.";
	public String playerNotFound = " <gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>Player could not be found because he is offline or does not exist.";
	public String samePlayer = " <gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>You cannot use this player in this scenario.";

	public MainCommandMessagesConfig mainCommand = new MainCommandMessagesConfig();
	public ReloadCommandMessagesConfig reloadCommand = new ReloadCommandMessagesConfig();
	public AnnounceCommandMessagesConfig announceCommand = new AnnounceCommandMessagesConfig();
	public PrivateMessageCommandMessagesConfig privateMessageCommand = new PrivateMessageCommandMessagesConfig();
	public BubbleCommandMessagesConfig bubbleCommand = new BubbleCommandMessagesConfig();
	public ReplyCommandMessagesConfig replyCommand = new ReplyCommandMessagesConfig();
}
