package me.whereareiam.socialismus.api.event.chat;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AfterChatEvent extends Event {
	private static final HandlerList HANDLER_LIST = new HandlerList();

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLER_LIST;
	}
}
