package me.whereareiam.socialismus.module.network.common.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.kyori.adventure.text.Component;

/**
 * A chat message as it travels to the other nodes: who wrote it, in which chat, what it says, and the format it was
 * given where it was written.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatRecord {
	private int id;
	private String sender;
	private String senderName;
	private String senderServer;
	private String chat;
	private Component content;
	private Component format;
}
