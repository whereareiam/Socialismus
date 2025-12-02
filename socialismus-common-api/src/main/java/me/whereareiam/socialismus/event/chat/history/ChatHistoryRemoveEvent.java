package me.whereareiam.socialismus.event.chat.history;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.whereareiam.socialismus.event.base.Event;

@Getter
@AllArgsConstructor
public abstract class ChatHistoryRemoveEvent implements Event {
	private final String origin;
}
