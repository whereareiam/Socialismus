package me.whereareiam.socialismus.api.model.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.whereareiam.socialismus.api.model.chat.message.ChatMessage;

/**
 * Wire-format for chat sync.
 * • `origin`   – server name / id that sent the packet
 * • `message`  – the full chat message
 * (recipients can be empty; each server will recalc its own)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatSyncPacket {
	private String origin;
	private String content;
	private ChatMessage message;
}
