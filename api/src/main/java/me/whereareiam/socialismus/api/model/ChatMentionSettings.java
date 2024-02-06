package me.whereareiam.socialismus.api.model;

public class ChatMentionSettings {
	public boolean enabled = true;
	public boolean mentionSelf = false;
	public boolean mentionAll = false;
	public boolean mentionNearby = false;
	public int maxMentions = 5;
	public int radius = 100;
}
