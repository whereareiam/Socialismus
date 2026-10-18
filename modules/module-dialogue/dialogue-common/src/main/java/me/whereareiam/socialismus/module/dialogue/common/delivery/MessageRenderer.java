package me.whereareiam.socialismus.module.dialogue.common.delivery;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.DialogueFormat;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.model.config.MessagingFormats;
import me.whereareiam.socialismus.module.dialogue.common.MessagingRequirements;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Shows a private message to its sender and its recipient, each in the last format of their list in
 * {@code formats/messaging} whose requirements the two participants meet.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MessageRenderer {
	private final Provider<MessagingFormats> formats;
	private final MessagingRequirements requirements;

	/**
	 * Shows the sender their message in a {@code sent} format.
	 *
	 * @param message   the message, whose sender sees it
	 * @param recipient the recipient, or {@code null} when they are not known to this server
	 */
	public void showSent(@NotNull Message message, @Nullable SocialismusPlayer recipient) {
		show(message.getSender(), message, recipient, formats.get().getSent());
	}

	/**
	 * Shows the recipient the message in a {@code received} format.
	 *
	 * @param message   the message
	 * @param recipient the recipient, who sees it
	 */
	public void showReceived(@NotNull Message message, @NotNull SocialismusPlayer recipient) {
		show(recipient, message, recipient, formats.get().getReceived());
	}

	private void show(
			SocialismusPlayer viewer,
			Message message,
			@Nullable SocialismusPlayer recipient,
			@Nullable List<DialogueFormat> formats
	) {
		DialogueFormat format = select(message, recipient, formats);
		if (format == null) {
			Logger.debug("No format of formats/messaging matches the private message to %s", viewer.getUsername());
			return;
		}

		Component shown = Serializer.serialize(SerializerContent.builder()
				.receiver(viewer)
				.message(format.getFormat())
				.placeholders(Map.of(
						"senderName", message.getSender().getUsername(),
						"recipientName", message.getRecipientName()
				))
				.build());
		// The message goes in after the format is parsed, so what a player wrote is shown as written and never read
		// as formatting, such as a click that runs a command.
		viewer.sendMessage(shown.replaceText(TextReplacementConfig.builder()
				.matchLiteral(Serializer.placeholder("message"))
				.replacement(Component.text(message.getContent()))
				.build()));
	}

	private @Nullable DialogueFormat select(
			Message message,
			@Nullable SocialismusPlayer recipient,
			@Nullable List<DialogueFormat> formats
	) {
		if (formats == null) return null;

		for (DialogueFormat format : formats.reversed())
			if (requirements.meets(format.getRequirements(), message.getSender(), recipient))
				return format;

		return null;
	}
}
