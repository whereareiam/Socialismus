package me.whereareiam.socialismus.core.config.module.chatmention.tag;

import java.util.List;

public class NearbyTagSettingsConfig {
	public String permission = "socialismus.chat.mention.nearby";
	public List<String> tags = List.of("@nearby", "@near");
	public String format = "<yellow>{usedTag}</yellow>";
}
