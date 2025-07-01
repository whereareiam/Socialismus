package me.whereareiam.socialismus.api.event.chathistory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.whereareiam.socialismus.api.input.event.base.Event;

@Getter
@AllArgsConstructor
public abstract class ChatHistoryRemoveEvent implements Event {
	private final String origin;
}
