package me.whereareiam.socialismus.event.chat.history;

import lombok.Getter;
import me.whereareiam.socialismus.event.base.Event;
import me.whereareiam.socialismus.model.chat.history.HistorySelection;
import org.jetbrains.annotations.NotNull;

/**
 * Called when messages were removed from the chat history, such as by {@code /socialismus clear}.
 */
@Getter
public abstract class ChatHistoryRemoveEvent implements Event {
	/**
	 * The selection that was removed: the very instance passed to
	 * {@link me.whereareiam.socialismus.service.chat.ChatHistoryService#remove(HistorySelection)}, so whoever made a
	 * removal can tell its own apart.
	 */
	private final @NotNull HistorySelection selection;

	protected ChatHistoryRemoveEvent(@NotNull HistorySelection selection) {
		this.selection = selection;
	}
}
