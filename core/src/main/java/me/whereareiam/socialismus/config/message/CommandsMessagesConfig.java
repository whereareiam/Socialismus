package me.whereareiam.socialismus.config.message;

public class CommandsMessagesConfig {
    public String wrongSyntax = " <gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>You used the command <red>incorrectly.";
    public String unknownCommand = " <gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>You used a command that <red>doesn't <white>exist.";
    public String errorOccurred = "An error occurred while executing the command.";
    public String missingArgument = " <gold>ꜱᴏᴄɪᴀʟɪꜱᴍᴜꜱ <dark_gray>| <white>You did not <red>specify the required argument <white>to execute the command.";

    public MainCommandMessagesConfig mainCommand = new MainCommandMessagesConfig();
    public ReloadCommandMessagesConfig reloadCommand = new ReloadCommandMessagesConfig();
}
