package me.whereareiam.socialismus.config;

import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.language.object.YamlSerializable;

public class MessagesConfig extends YamlSerializable {
    private static final SerializerConfig MESSAGES = new SerializerConfig.Builder().build();

    public MessagesConfig() {
        super(MessagesConfig.MESSAGES);
    }

    public String noPermission = "You don't have enough rights.";

    public Commands commands = new Commands();

    public static class Commands {
        public String wrongSyntax = "You used the command incorrectly.";
        public String unknownCommand = "You used a command that doesn't exist.";
        public String errorOccurred = "An error occurred while executing the command.";
        public String missingArgument = "You did not specify the required argument to execute the command.";

        public ReloadCommand reloadCommand = new ReloadCommand();
        public static class ReloadCommand {
            public String reloadedSuccessfully = "Plugin was successfully reloaded";
        }
    }
}