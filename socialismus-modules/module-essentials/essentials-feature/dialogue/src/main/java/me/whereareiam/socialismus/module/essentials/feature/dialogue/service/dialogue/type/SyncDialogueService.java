package me.whereareiam.socialismus.module.essentials.feature.dialogue.service.dialogue.type;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueMessages;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.model.Dialogue;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.dialogue.AbstractDialogueService;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.sync.DialogueNetworkBridge;

@Singleton
public final class SyncDialogueService extends AbstractDialogueService {
	private final DialogueNetworkBridge bridge;

	@Inject
	public SyncDialogueService(
			Provider<DialogueMessages> messages,
			DialogueNetworkBridge bridge
	) {
		super(messages);
		this.bridge = bridge;
	}

	public void publish(Dialogue pm) {
		bridge.publish(pm);
	}
}
