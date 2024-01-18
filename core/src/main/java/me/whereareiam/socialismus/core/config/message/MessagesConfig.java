package me.whereareiam.socialismus.core.config.message;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.config.message.command.CommandsMessagesConfig;
import net.elytrium.serializer.language.object.YamlSerializable;

@Singleton
public class MessagesConfig extends YamlSerializable {
	public ChatMessagesConfig chat = new ChatMessagesConfig();
	public SwapperMessagesConfig swapper = new SwapperMessagesConfig();
	public BubbleChatMessagesConfig bubblechat = new BubbleChatMessagesConfig();
	public CommandsMessagesConfig commands = new CommandsMessagesConfig();
}