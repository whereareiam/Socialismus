package me.whereareiam.socialismus.input.event.chat.history;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.whereareiam.socialismus.input.event.base.Event;

@Getter
@AllArgsConstructor
public abstract class ChatHistoryRemoveEvent implements Event {
	private final String origin;
}
