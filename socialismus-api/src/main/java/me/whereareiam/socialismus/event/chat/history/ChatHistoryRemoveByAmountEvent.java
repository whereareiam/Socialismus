package me.whereareiam.socialismus.event.chat.history;

import lombok.Getter;

@Getter
public class ChatHistoryRemoveByAmountEvent extends ChatHistoryRemoveEvent {
	private final int amount;

	public ChatHistoryRemoveByAmountEvent(int amount) {
		this.amount = amount;
	}
}
