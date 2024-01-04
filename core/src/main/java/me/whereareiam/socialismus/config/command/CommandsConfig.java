package me.whereareiam.socialismus.config.command;

import com.google.inject.Singleton;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.language.object.YamlSerializable;

@Singleton
public class CommandsConfig extends YamlSerializable {

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
                    @CommentValue(" syntax: this parameter is printed if the command is used incorrectly."),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" enabled: almost all commands can be enabled or disabled with this option."),
                    @CommentValue(type = CommentValue.Type.NEW_LINE),
            },
            at = Comment.At.PREPEND
    )
    public MainCommandConfig mainCommand = new MainCommandConfig();
    public ReloadCommandConfig reloadCommand = new ReloadCommandConfig();
    public AnnounceCommandConfig announceCommand = new AnnounceCommandConfig();
    public PrivateMessageCommandConfig privateMessageCommand = new PrivateMessageCommandConfig();
    public BubbleCommandConfig bubbleCommand = new BubbleCommandConfig();
}
