package me.whereareiam.socialismus.event.chat.history;

import me.whereareiam.socialismus.event.base.Event;

/**
 * Called when messages are removed from the chat history on this server, such as by {@code /socialismus clear}. A
 * removal that another server of a network sent is applied without this event, so a listener that passes removals on
 * never sends them back.
 */
public abstract class ChatHistoryRemoveEvent implements Event {
}
