package me.whereareiam.socialismus.event.chat.history;

import lombok.Getter;

@Getter
public class ChatHistoryRemoveByPlayerEvent extends ChatHistoryRemoveEvent {
	private final String username;

	public ChatHistoryRemoveByPlayerEvent(String origin, String username) {
		super(origin);
		this.username = username;
	}
}
