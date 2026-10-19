package me.whereareiam.socialismus.event.chat.history;

import lombok.Getter;

@Getter
public class ChatHistoryRemoveByIdEvent extends ChatHistoryRemoveEvent {
	private final int id;

	public ChatHistoryRemoveByIdEvent(int id) {
		this.id = id;
	}
}
