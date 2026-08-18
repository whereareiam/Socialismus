package me.whereareiam.socialismus.module.essentials.feature.dialogue.service.dialogue.type;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueMessages;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.model.Dialogue;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.dialogue.AbstractDialogueService;

import java.util.Map;

@Singleton
public final class LocalDialogueService extends AbstractDialogueService {
	private final PlayerRegistry playerRegistry;

	@Inject
	public LocalDialogueService(
			PlayerRegistry playerRegistry,
			Provider<DialogueMessages> messages
	) {
		super(messages);
		this.playerRegistry = playerRegistry;
	}

	public boolean deliverLocally(Dialogue pm) {
		return playerRegistry.getPlayerData(pm.getRecipientName()).map(recipient -> {
			DialogueMessages.Commands.Message cfg =
					messages.get().getCommands().getMessage();

			Map<String, String> ph = placeholders(pm);

			// Only send to sender if they're actually online on this server
			// (handles cross-server messages where sender is on another server)
			playerRegistry.getPlayerData(pm.getSender().getUsername()).ifPresent(sender ->
					sender.sendMessage(render(sender, ph, cfg.getSenderFormat()))
			);

			recipient.sendMessage(render(recipient, ph, cfg.getRecipientFormat()));
			return true;
		}).orElse(false);
	}

	public void notifyUndeliverable(Dialogue pm) {
		// Only notify sender if they're actually online on this server
		playerRegistry.getPlayerData(pm.getSender().getUsername()).ifPresent(sender ->
				sender.sendMessage(noRecipient(sender, pm.getRecipientName()))
		);
	}
}
