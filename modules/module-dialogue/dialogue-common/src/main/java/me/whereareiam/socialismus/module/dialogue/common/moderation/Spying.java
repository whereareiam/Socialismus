package me.whereareiam.socialismus.module.dialogue.common.moderation;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.SpyStore;
import me.whereareiam.socialismus.module.dialogue.api.event.DialogueDeliveredEvent;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.common.SenderPrivileges;
import me.whereareiam.socialismus.module.dialogue.common.delivery.MessageRenderer;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * Shows the spies of the recipient's server each private message delivered there, unless privacy mode is on or a
 * participant meets {@code moderation.spy.exempt}. A spy who takes part in the message sees only their own copy.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class Spying implements EventListener {
	private final Provider<DialogueSettings> settings;
	private final SpyStore spies;
	private final SenderPrivileges privileges;
	private final PlayerRegistry playerRegistry;
	private final MessageRenderer renderer;

	@SocialisticEvent
	public void onDelivered(@NotNull DialogueDeliveredEvent event) {
		DialogueSettings.Moderation moderation = settings.get().getModeration();
		if (moderation.isPrivacy() || !moderation.getSpy().isEnabled()) return;

		Message message = event.getMessage();
		SocialismusPlayer recipient = event.getRecipient();
		if (message.isHiddenFromSpies() || privileges.meets(moderation.getSpy().getExempt(), recipient)) return;

		for (SocialismusPlayer spy : playerRegistry.getPlayers()) {
			if (!spies.isSpying(spy.getUniqueId())) continue;
			if (spy.getUniqueId().equals(recipient.getUniqueId())
					|| spy.getUniqueId().equals(message.getSender().getUniqueId())) continue;

			renderer.showSpied(message, recipient, spy);
		}
	}
}
