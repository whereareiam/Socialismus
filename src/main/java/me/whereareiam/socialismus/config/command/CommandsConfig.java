package me.whereareiam.socialismus.config.command;

import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.language.object.YamlSerializable;

public class CommandsConfig extends YamlSerializable {
    private static final SerializerConfig COMMANDS = new SerializerConfig.Builder().build();

    public CommandsConfig() {
        super(CommandsConfig.COMMANDS);
    }

    public String mainCommand = "socialismus|social";

    public ReloadCommandConfig reloadCommand = new ReloadCommandConfig();
}
