package me.whereareiam.socialismus.api.event;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class Event extends org.bukkit.event.Event {
	private static final HandlerList HANDLER_LIST = new HandlerList();

	public Event() {
		super(true);
	}

	/**
	 * @return The handler list.
	 */
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

	/**
	 * @return The handler list.
	 */
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLER_LIST;
	}
}
