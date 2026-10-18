package me.whereareiam.socialismus.module.dialogue.common.conversation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.chat.ChatReceivedEvent;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.module.dialogue.api.DialogueService;
import me.whereareiam.socialismus.util.ComponentUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Takes what a player in conversation mode writes in chat out of chat and sends it to their partner as a private
 * message.
 */
@Singleton
public class ConversationChat implements EventListener {
	private final DialogueService dialogueService;

	@Inject
	public ConversationChat(@NotNull DialogueService dialogueService) {
		this.dialogueService = dialogueService;
	}

	@SocialisticEvent
	public void onChat(@NotNull ChatReceivedEvent event) {
		ChatMessage message = event.getChatMessage();
		dialogueService.conversationPartner(message.getSender()).ifPresent(partner -> {
			event.setCancelled(true);
			dialogueService.send(message.getSender(), partner, ComponentUtil.toPlain(message.getContent()));
		});
	}
}
