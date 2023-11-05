package me.whereareiam.socialismus.config.command;

import com.google.inject.Singleton;
import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.language.object.YamlSerializable;

@Singleton
public class CommandsConfig extends YamlSerializable {
    private static final SerializerConfig COMMANDS = new SerializerConfig.Builder().build();
    public MainCommandConfig mainCommand = new MainCommandConfig();
    public ReloadCommandConfig reloadCommand = new ReloadCommandConfig();
    public PrivateMessageCommandConfig privateMessageCommand = new PrivateMessageCommandConfig();

    public CommandsConfig() {
        super(CommandsConfig.COMMANDS);
    }
}
