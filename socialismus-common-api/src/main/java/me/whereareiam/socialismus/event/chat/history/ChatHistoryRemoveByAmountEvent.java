package me.whereareiam.socialismus.event.chat.history;

import lombok.Getter;

@Getter
public class ChatHistoryRemoveByAmountEvent extends ChatHistoryRemoveEvent {
	private final int amount;

	public ChatHistoryRemoveByAmountEvent(String origin, int amount) {
		super(origin);
		this.amount = amount;
	}
}
