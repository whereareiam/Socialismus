package me.whereareiam.socialismus.api.model.chatmention.notification;

import net.kyori.adventure.bossbar.BossBar;

public class BossBarNotification {
	public BossBar.Color color = BossBar.Color.RED;
	public BossBar.Overlay style = BossBar.Overlay.PROGRESS;
	public int duration = 30;
	public String message = "You were mentioned!";
}
