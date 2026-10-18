package me.whereareiam.socialismus.module.dialogue.common.control;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.module.dialogue.api.IgnoreStore;
import me.whereareiam.socialismus.module.dialogue.api.MessageToggleStore;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.api.type.DeliveryStatus;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Decides, on the recipient's server, whether a recipient refuses a private message: because they turned messages
 * off, or because they ignore the sender. What only the sender's server can check, its requirements, travels on the
 * message; this server fills it in for the messages written here.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RecipientControl {
	private final Provider<DialogueSettings> settings;
	private final MessageToggleStore toggles;
	private final IgnoreStore ignores;
	private final RequirementEvaluatorService evaluator;

	/**
	 * Records on a message written on this server what the sender's requirements allow it on any server.
	 *
	 * @param message the message, whose sender is on this server
	 */
	public void grantPrivileges(@NotNull Message message) {
		DialogueSettings.Control control = settings.get().getControl();
		message.setBypassesToggle(meets(control.getToggle().getBypass(), message.getSender()));
		message.setExemptFromIgnore(meets(control.getIgnore().getExempt(), message.getSender()));
	}

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
		if (control.getToggle().isEnabled() && !message.isBypassesToggle() && toggles.isOff(recipient.getUniqueId()))
			return Optional.of(DeliveryStatus.MESSAGES_OFF);

		return Optional.empty();
	}

	/**
	 * Returns whether a player meets a group of {@code control}. Nobody meets an absent group; a group without
	 * requirements is met by everyone, as everywhere in Socialismus.
	 */
	public boolean meets(RequirementGroup group, @NotNull SocialismusPlayer player) {
		if (group == null) return false;

		return evaluator.check(group, player);
	}
}
