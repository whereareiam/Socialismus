package me.whereareiam.socialismus.config.command;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.roleplay.RolePlay;
import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.language.object.YamlSerializable;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class CommandsConfig extends YamlSerializable {
    private static final SerializerConfig COMMANDS = new SerializerConfig.Builder().build();

    @Comment(
            value = {
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" PARAMETER EXPLANATION:"),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" command: allows you to change the command and its aliases by printing all"),
                    @CommentValue(" aliases separated by | symbol."),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" permission: needed right for player/staff to use this specific command and"),
                    @CommentValue(" aliases. You can leave it empty to disable it."),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" enabled: almost all commands can be enabled or disabled with this option."),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" syntax: this parameter is printed if the command is used incorrectly."),
                    @CommentValue(type = CommentValue.Type.NEW_LINE),
            },
            at = Comment.At.PREPEND
    )
    public MainCommandConfig mainCommand = new MainCommandConfig();
    public ReloadCommandConfig reloadCommand = new ReloadCommandConfig();
    public PrivateMessageCommandConfig privateMessageCommand = new PrivateMessageCommandConfig();
    public List<RolePlay> rolePlayCommands = new ArrayList<>();

    public CommandsConfig() {
        super(CommandsConfig.COMMANDS);
    }
}
