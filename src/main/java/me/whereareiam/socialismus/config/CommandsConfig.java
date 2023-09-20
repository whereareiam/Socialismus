package me.whereareiam.socialismus.config;

import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.language.object.YamlSerializable;

public class CommandsConfig extends YamlSerializable {
    private static final SerializerConfig COMMANDS = new SerializerConfig.Builder().build();

    public CommandsConfig() {
        super(CommandsConfig.COMMANDS);
    }

    public String mainCommand = "socialismus|social";

    public ReloadCommand reloadCommand = new ReloadCommand();

    public static class ReloadCommand {
        public String subCommand = "reload|reboot|restart";
        public String permission = "socialismus.reload";
    }
}
