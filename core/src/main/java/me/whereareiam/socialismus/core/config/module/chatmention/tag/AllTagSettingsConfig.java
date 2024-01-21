package me.whereareiam.socialismus.core.config.module.chatmention.tag;

import java.util.List;

public class AllTagSettingsConfig {
	public String permission = "socialismus.chat.mention.all";
	public List<String> tags = List.of("@all", "@everyone");
	public String format = "<red>{usedTag}</red>";
}
