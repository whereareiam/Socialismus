package me.whereareiam.socialismus.config.command;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.roleplay.RolePlay;
import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.language.object.YamlSerializable;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class CommandsConfig extends YamlSerializable {
    private static final SerializerConfig COMMANDS = new SerializerConfig.Builder().build();
    public MainCommandConfig mainCommand = new MainCommandConfig();
    public ReloadCommandConfig reloadCommand = new ReloadCommandConfig();
    public PrivateMessageCommandConfig privateMessageCommand = new PrivateMessageCommandConfig();
    public List<RolePlay> rolePlayCommands = new ArrayList<>();

    public CommandsConfig() {
        super(CommandsConfig.COMMANDS);
    }
}
