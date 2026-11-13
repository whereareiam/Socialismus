package me.whereareiam.socialismus.event.chat.history;

import lombok.Getter;
import me.whereareiam.socialismus.model.chat.history.HistorySelection;
import org.jetbrains.annotations.NotNull;

@Getter
public class ChatHistoryRemoveByAmountEvent extends ChatHistoryRemoveEvent {
	private final int amount;

	public ChatHistoryRemoveByAmountEvent(@NotNull HistorySelection.Oldest selection) {
		super(selection);
		this.amount = selection.amount();
	}
}
