package me.whereareiam.socialismus.module.dialogue.common.moderation;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.PreferencesStore;
import me.whereareiam.socialismus.module.dialogue.api.event.DialogueDeliveredEvent;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.common.SenderPrivileges;
import me.whereareiam.socialismus.module.dialogue.common.delivery.MessageRenderer;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * Shows the spies each private message delivered on this server, unless privacy mode is on or a participant meets
 * {@code moderation.spy.exempt}. A spy who takes part in the message sees only their own copy. With
 * SocialismusNetwork, the spies on the other nodes see it as well, through {@link #showToSpies}.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class Spying implements EventListener {
	private final Provider<DialogueSettings> settings;
	private final PreferencesStore preferences;
	private final SenderPrivileges privileges;
	private final PlayerRegistry playerRegistry;
	private final MessageRenderer renderer;

	@SocialisticEvent
	public void onDelivered(@NotNull DialogueDeliveredEvent event) {
		if (isSpiedOn(event.getMessage(), event.getRecipient())) showToSpies(event.getMessage(), event.getRecipient());
	}

	/**
	 * Returns whether spies may see a message delivered to a recipient: privacy mode is off, spying is enabled, the
	 * sender did not hide it and the recipient is not exempt.
	 */
	public boolean isSpiedOn(@NotNull Message message, @NotNull SocialismusPlayer recipient) {
		DialogueSettings.Moderation moderation = settings.get().getModeration();
		if (moderation.isPrivacy() || !moderation.getSpy().isEnabled()) return false;

		return !message.isHiddenFromSpies() && !privileges.meets(moderation.getSpy().getExempt(), recipient);
	}

	/**
	 * Shows a message to the spies on this server, except to its sender and recipient.
	 *
	 * @param message   the message, whose sender may be on another node
	 * @param recipient its recipient, who may be on another node
	 */
	public void showToSpies(@NotNull Message message, @NotNull SocialismusPlayer recipient) {
		for (SocialismusPlayer spy : playerRegistry.getPlayers()) {
			if (!preferences.get(spy.getUniqueId()).isSpying()) continue;
			if (spy.getUniqueId().equals(recipient.getUniqueId())
					|| spy.getUniqueId().equals(message.getSender().getUniqueId())) continue;

			renderer.showSpied(message, recipient, spy);
		}
	}
}
