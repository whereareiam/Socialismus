package me.whereareiam.socialismus.config.message;

public class CommandsMessagesConfig {
    public String wrongSyntax = "You used the command incorrectly.";
    public String unknownCommand = "You used a command that doesn't exist.";
    public String errorOccurred = "An error occurred while executing the command.";
    public String missingArgument = "You did not specify the required argument to execute the command.";

    public ReloadCommandMessagesConfig reloadCommand = new ReloadCommandMessagesConfig();
}
