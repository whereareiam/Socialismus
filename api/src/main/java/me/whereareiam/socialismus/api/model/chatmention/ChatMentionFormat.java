package me.whereareiam.socialismus.api.model.chatmention;

import java.util.List;

public class ChatMentionFormat {
	public boolean enabled = true;
	public String permission = "socialismus.chat.mention.1";
	public String format = "<red>@{playerName}</red>";
	public List<String> hoverFormat = List.of("<yellow>Hi, @{playerName}</yellow>");
}
