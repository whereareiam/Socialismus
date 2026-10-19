package me.whereareiam.socialismus.module.dialogue.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A private message as it travels to the node its recipient is on.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageRecord {
	private String id;
	private String target;
	private String sender;
	private String senderName;
	private String senderServer;
	private String recipientName;
	private String content;
	private long timestamp;
	private String conversationId;
	private boolean bypassesToggle;
	private boolean exemptFromIgnore;
	private boolean hiddenFromSpies;
}
