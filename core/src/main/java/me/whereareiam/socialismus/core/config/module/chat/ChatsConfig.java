package me.whereareiam.socialismus.core.config.module.chat;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.chat.Chat;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.language.object.YamlSerializable;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class ChatsConfig extends YamlSerializable {
		@Comment(
						value = {
										@CommentValue(type = CommentValue.Type.TEXT),
										@CommentValue(" CHATS DOCUMENTATION:"),
										@CommentValue(type = CommentValue.Type.TEXT),
										@CommentValue(" Here you can create as many Chats as you like."),
										@CommentValue(" chats:"),
										@CommentValue("   Here you can specify a chat id, it should be unique and not used in any other chat."),
										@CommentValue("   - id: \"example\""),
										@CommentValue(type = CommentValue.Type.TEXT),
										@CommentValue("     Here you can specify how you want to use the chat."),
										@CommentValue("     usage:"),
										@CommentValue("       In type you need to specify the method that will be used to trigger the chat."),
										@CommentValue("       Possible values:"),
										@CommentValue("         - SYMBOL (using a symbol in the chat)"),
										@CommentValue("         - COMMAND (using a command)"),
										@CommentValue("         - SYMBOL_COMMAND (combining these 2 options)"),
										@CommentValue("       type: SYMBOL_COMMAND"),
										@CommentValue("       symbol: \"!\""),
										@CommentValue("       command: \"example\""),
										@CommentValue(type = CommentValue.Type.TEXT),
										@CommentValue("     Here you can specify multiple formats that will be used by the permission."),
										@CommentValue("     Format allows you to specify the design of the final message that will be shown"),
										@CommentValue("     to the players. You can use PlaceholderAPI placeholders, MiniMessage formatting and"),
										@CommentValue("     internal {playerName}/{message} placeholders."),
										@CommentValue("     formats:"),
										@CommentValue("       - format: <gold><bold><globalChat>G</globalChat></bold></gold> <dark_gray>| <gray><click:run_command:/tpa {playerName}><playerInformation>{playerName}</playerInformation></click>: <white><messageInformation>{message}</messageInformation>"),
										@CommentValue("         permission: \"\""),
										@CommentValue("         sound: null"),
										@CommentValue(type = CommentValue.Type.TEXT),
										@CommentValue("     This setting allows you to determine whether swapper can be used or not."),
										@CommentValue("     enable-swapper: true"),
										@CommentValue(type = CommentValue.Type.TEXT),
										@CommentValue("     Mentions section, controls how player mentions work in this chat."),
										@CommentValue("     mentions:"),
										@CommentValue("       Option to specify whether to enable or disable mentioning."),
										@CommentValue("       enabled: true"),
										@CommentValue("       This option allows the sender of a message to mention themselves."),
										@CommentValue("       mention-self: false"),
										@CommentValue("       This option allows the sender of a message to mention all players."),
										@CommentValue("       mention-all: false"),
										@CommentValue("       This option allows the sender of a message to mention nearby players."),
										@CommentValue("       mention-nearby: false"),
										@CommentValue("       Limit on how many players can be mentioned at once in this chat, does not apply to @all mentions."),
										@CommentValue("       max-mentions: 5"),
										@CommentValue("       Radius for @nearby mention"),
										@CommentValue("       radius: 100"),
										@CommentValue(type = CommentValue.Type.TEXT),
										@CommentValue("     Here you can specify the requirements for using this chat."),
										@CommentValue("     requirements:"),
										@CommentValue("       This option allows you to disable requirements check, it is advised to think about if you"),
										@CommentValue("       need the requirements or not, because if you disable the performance will be much better."),
										@CommentValue("       enabled: true"),
										@CommentValue("       sender:"),
										@CommentValue("         This option allows you to specify the minimum online players required to use this chat."),
										@CommentValue("         min-online: 0"),
										@CommentValue("         Here you can set a number of symbols that players must write to be able to send this"),
										@CommentValue("         message in this chat."),
										@CommentValue("         symbol-count-threshold: 0"),
										@CommentValue("         Here you can specify the permission that the player must have to use this chat."),
										@CommentValue("         use-permission: \"i.love.whereareiam\""),
										@CommentValue("         Here you can specify the worlds in which the player can use this chat."),
										@CommentValue("         worlds:"),
										@CommentValue("           - \"world\""),
										@CommentValue("           - \"world_nether\""),
										@CommentValue("           - \"world_the_end\""),
										@CommentValue(type = CommentValue.Type.TEXT),
										@CommentValue("       recipient:"),
										@CommentValue("         This option allows you to specify the radius in which the player will see the message."),
										@CommentValue("         radius: -1"),
										@CommentValue("         This option allows you to specify whether the player will see his own message."),
										@CommentValue("         see-own-message: true"),
										@CommentValue("         This option allows you to specify the permission that the player must have to see this"),
										@CommentValue("         see-permission: \"whereareiam.is.the.best\""),
										@CommentValue("         Here you can specify the worlds in which the player can see this chat."),
										@CommentValue("         worlds:"),
										@CommentValue("           - \"world\""),
										@CommentValue("           - \"world_nether\""),
										@CommentValue("           - \"world_the_end\""),
										@CommentValue(type = CommentValue.Type.NEW_LINE),
						},
						at = Comment.At.PREPEND
		)
		public List<Chat> chats = new ArrayList<>();
}
