package me.whereareiam.socialismus.config.command;

import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.language.object.YamlSerializable;

public class CommandsConfig extends YamlSerializable {
    private static final SerializerConfig COMMANDS = new SerializerConfig.Builder().build();
    public MainCommandConfig mainCommand = new MainCommandConfig();
    public ReloadCommandConfig reloadCommand = new ReloadCommandConfig();

    public CommandsConfig() {
        super(CommandsConfig.COMMANDS);
    }
}
