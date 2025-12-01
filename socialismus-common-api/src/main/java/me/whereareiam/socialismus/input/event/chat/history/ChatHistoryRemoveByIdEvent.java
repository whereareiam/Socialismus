package me.whereareiam.socialismus.input.event.chat.history;

import lombok.Getter;

@Getter
public class ChatHistoryRemoveByIdEvent extends ChatHistoryRemoveEvent {
	private final int id;

	public ChatHistoryRemoveByIdEvent(String origin, int id) {
		super(origin);
		this.id = id;
	}
}
