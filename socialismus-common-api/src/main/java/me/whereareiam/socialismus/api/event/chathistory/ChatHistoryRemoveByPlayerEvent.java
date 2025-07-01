package me.whereareiam.socialismus.api.event.chathistory;

import lombok.Getter;

@Getter
public class ChatHistoryRemoveByPlayerEvent extends ChatHistoryRemoveEvent {
	private final String username;

	public ChatHistoryRemoveByPlayerEvent(String origin, String username) {
		super(origin);
		this.username = username;
	}
}
