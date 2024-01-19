package me.whereareiam.socialismus.api.model.chatmention.notification;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

public class BossBarNotification {
	public BarColor color = BarColor.RED;
	public BarStyle style = BarStyle.SOLID;
	public int duration = 30;
	public String title = "You were mentioned!";
}
