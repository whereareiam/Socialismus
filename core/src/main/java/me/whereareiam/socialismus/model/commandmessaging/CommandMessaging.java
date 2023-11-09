package me.whereareiam.socialismus.model.commandmessaging;

public class CommandMessaging {
    public boolean enabled;
    public String command;
    public String format;
    public String syntax;
    public String description;
    public CommandMessagingRequirements requirements = new CommandMessagingRequirements();
}
