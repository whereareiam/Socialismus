package me.whereareiam.socialismus.module.essentials.feature.dialogue.service.dialogue;

import com.google.inject.Provider;
import lombok.AllArgsConstructor;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueMessages;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.model.Dialogue;
import net.kyori.adventure.text.Component;

import java.util.Map;

@AllArgsConstructor
public abstract class AbstractDialogueService {
	protected final Provider<DialogueMessages> messages;

	protected Map<String, String> placeholders(Dialogue pm) {
		return Map.of(
				"{senderName}", pm.getSender().getUsername(),
				"{recipientName}", pm.getRecipientName(),
				"{message}", pm.getContent()
		);
	}

	protected Component render(
			SocialismusPlayer viewer, Map<String, String> placeholders, String template
	) {
		return Serializer.serialize(SerializerContent.builder()
				.receiver(viewer)
				.message(template)
				.placeholders(placeholders)
				.build());
	}

	protected Component noRecipient(SocialismusPlayer sender, String recipient) {
		String tpl = messages.get().getCommands().getMessage().getNoRecipient();
		return Serializer.serialize(SerializerContent.builder()
				.receiver(sender)
				.message(tpl)
				.placeholder("{recipientName}", recipient)
				.build());
	}
}
