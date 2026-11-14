package me.whereareiam.socialismus.event.chat.history;

import lombok.Getter;
import me.whereareiam.socialismus.model.chat.history.HistorySelection;
import org.jetbrains.annotations.NotNull;

@Getter
public class ChatHistoryRemoveByIdEvent extends ChatHistoryRemoveEvent {
	private final int id;

	public ChatHistoryRemoveByIdEvent(@NotNull HistorySelection.Message selection) {
		super(selection);
		this.id = selection.id();
	}
}
