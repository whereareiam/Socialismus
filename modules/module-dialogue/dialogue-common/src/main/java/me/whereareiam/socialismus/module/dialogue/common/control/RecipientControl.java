package me.whereareiam.socialismus.module.dialogue.common.control;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.IgnoreStore;
import me.whereareiam.socialismus.module.dialogue.api.PreferencesStore;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.api.type.DeliveryStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Decides, on the recipient's server, whether a recipient refuses a private message: because they turned messages
 * off, or because they ignore the sender. What only the sender's server can check, its requirements, travels on the
 * message, recorded by {@link me.whereareiam.socialismus.module.dialogue.common.SenderPrivileges}.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RecipientControl {
	private final Provider<DialogueSettings> settings;
	private final PreferencesStore preferences;
	private final IgnoreStore ignores;

	/**
	 * Returns why the recipient refuses the message, or empty when they take it.
	 *
	 * @param message   the message
	 * @param recipient the recipient, on this server
	 */
	@NotNull
	public Optional<DeliveryStatus> refusal(@NotNull Message message, @NotNull SocialismusPlayer recipient) {
		DialogueSettings.Control control = settings.get().getControl();
		if (control.getIgnore().isEnabled() && !message.isExemptFromIgnore()
				&& ignores.ignores(recipient.getUniqueId(), message.getSender().getUniqueId()))
			return Optional.of(DeliveryStatus.IGNORED);
		if (control.getToggle().isEnabled() && !message.isBypassesToggle() && preferences.get(recipient.getUniqueId()).isMessagesOff())
			return Optional.of(DeliveryStatus.MESSAGES_OFF);

		return Optional.empty();
	}
}
