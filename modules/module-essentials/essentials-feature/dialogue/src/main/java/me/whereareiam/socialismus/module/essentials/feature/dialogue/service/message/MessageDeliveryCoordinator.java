package me.whereareiam.socialismus.module.essentials.feature.dialogue.service.message;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueSettings;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.model.Dialogue;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.model.Message;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.dialogue.type.LocalDialogueService;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.dialogue.type.SyncDialogueService;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.type.DeliveryStatus;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MessageDeliveryCoordinator {

	private final LocalDialogueService localDialogueService;
	private final SyncDialogueService syncDialogueService;
	private final Provider<DialogueSettings> settings;

	public boolean deliverMessage(Dialogue dialogue) {
		return localDialogueService.deliverLocally(dialogue);
	}

	public void updateMessageStatus(Message message, boolean delivered) {
		if (delivered) {
			message.setDeliveryStatus(DeliveryStatus.DELIVERED_LOCALLY);
			return;
		}

		message.setDeliveryStatus(DeliveryStatus.RECIPIENT_OFFLINE);
	}

	public void syncMessageToOtherServers(Dialogue dialogue) {
		dialogue.setOrigin(Constants.Synchronization.IDENTIFIER);
		syncDialogueService.publish(dialogue);
	}

	public void handleUndeliverableMessage(Dialogue dialogue, boolean delivered) {
		if (!delivered && !settings.get().getSynchronization().isEnabled()) {
			localDialogueService.notifyUndeliverable(dialogue);
		}
	}

	public void processMessageDelivery(Dialogue dialogue, Message message) {
		boolean delivered = deliverMessage(dialogue);
		updateMessageStatus(message, delivered);
		syncMessageToOtherServers(dialogue);
		handleUndeliverableMessage(dialogue, delivered);
	}
}
