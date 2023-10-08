package me.whereareiam.socialismus.config.chat;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.feature.chat.Chat;
import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.language.object.YamlSerializable;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class ChatsConfig extends YamlSerializable {
    private static final SerializerConfig CHATS = new SerializerConfig.Builder().build();

    @Comment(
            value = {
                    @CommentValue(" Example configuration:"),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" chats:"),
                    @CommentValue("   - id: \"global\""),
                    @CommentValue("     see-permission: \"privilege.1\""),
                    @CommentValue("     write-permission: \"privilege.2\""),
                    @CommentValue("     chat-symbol: \"!\""),
                    @CommentValue("     radius: 50"),
                    @CommentValue("     message-format: \"%luckperms_prefix%%display_name%: {message}\""),
                    @CommentValue("     hover-format: "),
                    @CommentValue("       - \" Some text1 \""),
                    @CommentValue("       - \" Some text2 \""),
                    @CommentValue("     worlds: "),
                    @CommentValue("       - \"world\""),
                    @CommentValue("       - \"world_nether\""),
                    @CommentValue("       - \"world_the_end\""),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" Explanation:"),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" id -                Chat id/tag that is used within the plugin for specific actions, this id is also"),
                    @CommentValue("                     used in placeholders to display information."),
                    @CommentValue("                     **It should be specified, you can use letters and numbers**"),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" see-permission -    The permission a user needs to see a message from this chat."),
                    @CommentValue("                     **It is not necessary to specify**"),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" write-permission -  The permission a user needs to write a message to this chat."),
                    @CommentValue("                     **It is not necessary to specify**"),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" chat-symbol -       The symbol that you need to put in front of the message to send it to the desired chat."),
                    @CommentValue("                     **It is not necessary to specify**"),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" message-format -    The symbol that you need to put in front of the message to send it to the desired chat."),
                    @CommentValue("                     **It is not necessary to specify**"),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" hover-format -      Message that will be displayed when you hover over a message from the chat."),
                    @CommentValue("                     **It is not necessary to specify**"),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" worlds -            Worlds in which chat will work and be displayed."),
                    @CommentValue("                     **It is not necessary to specify**"),
                    @CommentValue(type = CommentValue.Type.NEW_LINE),
            },
            at = Comment.At.PREPEND
    )
    public List<Chat> chats = new ArrayList<>();

    public ChatsConfig() {
        super(ChatsConfig.CHATS);
    }
}
