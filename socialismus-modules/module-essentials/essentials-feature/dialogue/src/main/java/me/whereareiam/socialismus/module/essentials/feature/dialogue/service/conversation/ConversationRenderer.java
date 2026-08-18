package me.whereareiam.socialismus.module.essentials.feature.dialogue.service.conversation;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueMessages;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.dialogue.DialogueErrorHandler;
import net.kyori.adventure.text.Component;

import java.util.List;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConversationRenderer {
	private final Provider<DialogueMessages> messages;

	public Component renderConversationList(SocialismusPlayer sender, List<String> conversations) {
		StringBuilder list = new StringBuilder();
		list.append(messages.get().getCommands().getReply().getConversationListHeader()).append("\n");

		for (int i = 0; i < conversations.size(); i++) {
			String entry = messages.get().getCommands().getReply().getConversationListEntry()
					.replace("{index}", String.valueOf(i + 1))
					.replace("{player}", conversations.get(i));
			list.append(entry).append("\n");
		}

		return Serializer.serialize(sender, list.toString());
	}

	public Component renderReplyError(SocialismusPlayer sender, String errorType) {
		String template = switch (errorType) {
			case DialogueErrorHandler.ERROR_NO_PREVIOUS_SENDER ->
					messages.get().getCommands().getReply().getNoPreviousSender();
			case DialogueErrorHandler.ERROR_NO_CONVERSATIONS ->
					messages.get().getCommands().getReply().getNoConversations();
			default -> messages.get().getCommands().getReply().getNoPreviousSender();
		};
		return Serializer.serialize(sender, template);
	}

	public Component renderSelfMessageError(SocialismusPlayer sender) {
		return Serializer.serialize(
				sender,
				messages.get().getCommands().getMessage().getSamePlayer()
		);
	}
}
