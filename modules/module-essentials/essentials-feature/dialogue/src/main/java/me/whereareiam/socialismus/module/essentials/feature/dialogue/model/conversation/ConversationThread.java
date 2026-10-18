package me.whereareiam.socialismus.module.essentials.feature.dialogue.model.conversation;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.model.Message;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ConversationThread {
	private String id;

	private Set<String> participants;
	@Builder.Default
	private List<Message> messages = new ArrayList<>();

	private String lastSender;
	private Instant lastActivity;

	private boolean active;
	private int messageCount;

	public void addMessage(Message message) {
		this.messages.add(message);
		this.lastSender = message.getSender().getUsername();
		this.lastActivity = message.getTimestamp();
		this.messageCount++;
		message.setConversationId(this.id);
	}
}
