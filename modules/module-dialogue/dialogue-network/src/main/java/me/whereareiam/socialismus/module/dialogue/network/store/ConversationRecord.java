package me.whereareiam.socialismus.module.dialogue.network.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * A conversation as it is replicated: who talked and when, without the messages, which stay on the node they were
 * written on.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversationRecord {
	private String id;
	private List<String> participants;
	private String lastSender;
	private long lastActivity;
	private int messageCount;
}
