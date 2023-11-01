package me.whereareiam.socialismus.config.module.chat;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.model.Chat;
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
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" CHATS DOCUMENTATION:"),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" Here you can create as many Swappers as you like."),
                    @CommentValue(" chats:"),
                    @CommentValue("   Here you can specify a chat id, it should be unique and not used in any other chat."),
                    @CommentValue("   - id: \"global\""),
                    @CommentValue("     This field allows you to specify a symbol (§, $, &, etc.) that will be used to write"),
                    @CommentValue("     a message in this chat. You can leave it empty and no symbol will be used to write in"),
                    @CommentValue("     this chat."),
                    @CommentValue("     chat-symbol: \"!\""),
                    @CommentValue("     Message Format allows you to specify the design of the final message that will be shown"),
                    @CommentValue("     to the players. You can use PlaceholderAPI placeholders, MiniMessage formatting and"),
                    @CommentValue("     internal {playerName}/{message} placeholders."),
                    @CommentValue("     message-format: \"%luckperms_prefix%{playerName}: {message}\""),
                    @CommentValue("     In this list, you can specify what and how to show players when they hover their mouse"),
                    @CommentValue("     over the message format. You can use PlaceholderAPI placeholders and MiniMessage formatting"),
                    @CommentValue("     hover-format: "),
                    @CommentValue("       - \" Some text1 \""),
                    @CommentValue("       - \" Some text2 \""),
                    @CommentValue("     In this Requirement section, you can specify what the sender or recipient must do to send"),
                    @CommentValue("     or receive a message."),
                    @CommentValue("     requirements: "),
                    @CommentValue("       Here you can set the radius within which a player must be to receive a message. You can"),
                    @CommentValue("       set it to -1 to make it global (no radius)."),
                    @CommentValue("       radius: 50"),
                    @CommentValue("       Here you can set a permission that a player should have to be able to see a message from this"),
                    @CommentValue("       chat. You can leave it blank to disable the check."),
                    @CommentValue("       see-permission: \"privilege.1\""),
                    @CommentValue("       Here you can specify a permission that a player should have to be able to post a message in"),
                    @CommentValue("       this chat. You can leave it blank to disable the check."),
                    @CommentValue("       write-permission: \"privilege.2\""),
                    @CommentValue("       You can use this list to restrict your chat to specific worlds. Written worlds indicate that"),
                    @CommentValue("       you can use this chat there."),
                    @CommentValue("       worlds: "),
                    @CommentValue("         - \"world\""),
                    @CommentValue("         - \"world_nether\""),
                    @CommentValue("         - \"world_the_end\""),
                    @CommentValue(type = CommentValue.Type.NEW_LINE),
            },
            at = Comment.At.PREPEND
    )
    public List<Chat> chats = new ArrayList<>();

    public ChatsConfig() {
        super(ChatsConfig.CHATS);
    }
}
