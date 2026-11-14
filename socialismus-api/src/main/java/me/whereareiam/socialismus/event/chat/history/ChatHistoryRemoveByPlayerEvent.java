package me.whereareiam.socialismus.event.chat.history;

import lombok.Getter;
import me.whereareiam.socialismus.model.chat.history.HistorySelection;
import org.jetbrains.annotations.NotNull;

@Getter
public class ChatHistoryRemoveByPlayerEvent extends ChatHistoryRemoveEvent {
	private final String username;

	public ChatHistoryRemoveByPlayerEvent(@NotNull HistorySelection.OfPlayer selection) {
		super(selection);
		this.username = selection.player().getUsername();
	}

	public ChatHistoryRemoveByPlayerEvent(@NotNull HistorySelection.ByName selection) {
		super(selection);
		this.username = selection.username();
	}
}
