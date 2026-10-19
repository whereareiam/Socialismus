package me.whereareiam.socialismus.module.dialogue.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A private message delivered on one node, as it travels to the spies on the others.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpyRecord {
	private String id;
	private String sender;
	private String senderName;
	private String senderServer;
	private String recipient;
	private String recipientName;
	private String recipientServer;
	private String content;
	private long timestamp;
}
