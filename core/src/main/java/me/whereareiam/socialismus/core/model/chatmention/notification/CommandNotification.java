package me.whereareiam.socialismus.core.model.chatmention.notification;

public class CommandNotification {
	public String command = "tellraw {mentionedName} {\"text\":\"{mentionerName} mentioned you in chat!\",\"color\":\"red\"}";
}
