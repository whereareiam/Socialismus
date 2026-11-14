package me.whereareiam.socialismus.module.network.common.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * A chat message as it travels to the other nodes: who wrote it, in which chat, what it says, and the format it was
 * given where it was written, and the attributes modules carry with it, as JSON by their full key.
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
	private Map<String, String> attributes = new HashMap<>();
}
