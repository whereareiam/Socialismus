package me.whereareiam.socialismus.service.chat;

import me.whereareiam.socialismus.model.chat.history.HistorySelection;
import org.jetbrains.annotations.NotNull;

/**
 * Removes messages from the chat history and shows the players the history that is left.
 */
public interface ChatHistoryService {
	/**
	 * Removes the selected messages from the chat history. When any were removed, it calls the
	 * {@link me.whereareiam.socialismus.event.chat.history.ChatHistoryRemoveEvent} of the selection and shows the
	 * players the history that is left.
	 *
	 * @param selection the messages to remove
	 * @return how many messages were removed, {@code 0} when none were
	 */
	int remove(@NotNull HistorySelection selection);
}
