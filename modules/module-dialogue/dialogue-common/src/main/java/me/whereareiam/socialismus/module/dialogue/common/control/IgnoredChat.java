package me.whereareiam.socialismus.module.dialogue.common.control;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.chat.recipient.RecipientsSelectedEvent;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.IgnoreStore;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import org.jetbrains.annotations.NotNull;

/**
 * Keeps a player's chat from the players who ignore them, when {@code control.ignore.includesChat} is on.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class IgnoredChat implements EventListener {
	private final Provider<DialogueSettings> settings;
	private final IgnoreStore ignores;
	private final RecipientControl recipientControl;

	@SocialisticEvent
	public void onRecipientsSelected(@NotNull RecipientsSelectedEvent event) {
		DialogueSettings.Control.Ignore ignore = settings.get().getControl().getIgnore();
		if (!ignore.isEnabled() || !ignore.isIncludesChat()) return;

		SocialismusPlayer sender = event.getChatMessage().getSender();
		if (recipientControl.meets(ignore.getExempt(), sender)) return;

		event.getNewRecipients().removeIf(recipient -> ignores.ignores(recipient.getUniqueId(), sender.getUniqueId()));
	}
}
