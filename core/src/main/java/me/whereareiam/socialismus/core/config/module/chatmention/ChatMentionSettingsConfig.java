package me.whereareiam.socialismus.core.config.module.chatmention;

import me.whereareiam.socialismus.core.config.module.chatmention.tag.AllTagSettingsConfig;
import me.whereareiam.socialismus.core.config.module.chatmention.tag.NearbyTagSettingsConfig;

public class ChatMentionSettingsConfig {
	public String selfMentionPermission = "socialismus.chat.mention.self";
	public String maxMentionPermission = "socialismus.chat.mention.max.";
	public AllTagSettingsConfig allTagSettings = new AllTagSettingsConfig();
	public NearbyTagSettingsConfig nearbyTagSettings = new NearbyTagSettingsConfig();
}
