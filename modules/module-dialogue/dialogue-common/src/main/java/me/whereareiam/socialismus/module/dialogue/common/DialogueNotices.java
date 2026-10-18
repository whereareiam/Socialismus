package me.whereareiam.socialismus.module.dialogue.common;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.IgnoredPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueMessages;
import me.whereareiam.socialismus.module.dialogue.api.model.conversation.Conversation;
import me.whereareiam.socialismus.module.dialogue.api.type.DeliveryStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Tells a player why their private message was not sent, in the words of {@code messages}.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DialogueNotices {
	private final Provider<DialogueMessages> messages;

	public void samePlayer(@NotNull SocialismusPlayer sender) {
		send(sender, messaging().getSamePlayer(), Map.of());
	}

	public void notAllowed(@NotNull SocialismusPlayer sender, @NotNull String recipientName) {
		send(sender, messaging().getNotAllowed(), Map.of("recipientName", recipientName));
	}

	public void noRecipient(@NotNull SocialismusPlayer sender, @NotNull String recipientName) {
		send(sender, messaging().getNoRecipient(), Map.of("recipientName", recipientName));
	}

	public void noConversationMode(@NotNull SocialismusPlayer player) {
		send(player, messaging().getConversationMode().getUnavailable(), Map.of());
	}

	public void conversationEntered(@NotNull SocialismusPlayer player, @NotNull String partner) {
		send(player, messaging().getConversationMode().getEntered(), Map.of("recipientName", partner));
	}

	public void conversationLeft(@NotNull SocialismusPlayer player, @NotNull String partner) {
		send(player, messaging().getConversationMode().getLeft(), Map.of("recipientName", partner));
	}

	/**
	 * Tells the sender why their message did not reach the recipient.
	 *
	 * @param sender        the player who wrote it
	 * @param recipientName the recipient's name
	 * @param status        why: the recipient is offline, turned messages off, or ignores the sender
	 */
	public void undelivered(@NotNull SocialismusPlayer sender, @NotNull String recipientName, @NotNull DeliveryStatus status) {
		switch (status) {
			case MESSAGES_OFF -> send(sender, control().getToggle().getBlocked(), Map.of("recipientName", recipientName));
			case IGNORED -> send(sender, control().getIgnore().getBlocked(), Map.of("recipientName", recipientName));
			case RECIPIENT_OFFLINE -> noRecipient(sender, recipientName);
			default -> {
			}
		}
	}

	public void messagesToggled(@NotNull SocialismusPlayer player, boolean off) {
		send(player, off ? control().getToggle().getOff() : control().getToggle().getOn(), Map.of());
	}

	public void toggleUnavailable(@NotNull SocialismusPlayer player) {
		send(player, control().getToggle().getUnavailable(), Map.of());
	}

	public void ignoreChanged(@NotNull SocialismusPlayer player, @NotNull String target, boolean ignored) {
		DialogueMessages.Control.Ignore ignore = control().getIgnore();
		send(player, ignored ? ignore.getAdded() : ignore.getRemoved(), Map.of("ignoredName", target));
	}

	public void ignoreExempt(@NotNull SocialismusPlayer player, @NotNull String target) {
		send(player, control().getIgnore().getExempt(), Map.of("ignoredName", target));
	}

	public void ignoreUnavailable(@NotNull SocialismusPlayer player) {
		send(player, control().getIgnore().getUnavailable(), Map.of());
	}

	/**
	 * Shows a player whom they ignore.
	 */
	public void ignored(@NotNull SocialismusPlayer player, @NotNull List<IgnoredPlayer> ignored) {
		DialogueMessages.Control.Ignore.Listing listing = control().getIgnore().getList();
		if (ignored.isEmpty()) {
			send(player, listing.getEmpty(), Map.of());
			return;
		}

		StringBuilder notice = new StringBuilder(listing.getHeader());
		for (IgnoredPlayer other : ignored)
			notice.append('\n').append(listing.getEntry().replace("{ignoredName}", other.getName()));

		send(player, notice.toString(), Map.of());
	}

	public void spyToggled(@NotNull SocialismusPlayer player, boolean spying) {
		DialogueMessages.Moderation.Spy spy = messages.get().getModeration().getSpy();
		send(player, spying ? spy.getOn() : spy.getOff(), Map.of());
	}

	public void spyUnavailable(@NotNull SocialismusPlayer player) {
		send(player, messages.get().getModeration().getSpy().getUnavailable(), Map.of());
	}

	public void noConversations(@NotNull SocialismusPlayer sender) {
		send(sender, messaging().getReply().getNoConversations(), Map.of());
	}

	public void partnerOffline(@NotNull SocialismusPlayer sender, @NotNull String partner) {
		send(sender, messaging().getReply().getPartnerOffline(), Map.of("partner", partner));
	}

	/**
	 * Tells the sender they have no conversation to answer, followed by their recent conversations.
	 *
	 * @param sender        the player who replied
	 * @param conversations their conversations, the most recently active first
	 */
	public void noPartner(@NotNull SocialismusPlayer sender, @NotNull List<Conversation> conversations) {
		DialogueMessages.Messaging.Reply reply = messaging().getReply();

		StringBuilder notice = new StringBuilder(reply.getNoPartner());
		for (int i = 0; i < conversations.size(); i++)
			notice.append('\n').append(reply.getConversation()
					.replace("{index}", String.valueOf(i + 1))
					.replace("{partner}", conversations.get(i).partnerOf(sender.getUsername())));

		send(sender, notice.toString(), Map.of());
	}

	private DialogueMessages.Control control() {
		return messages.get().getControl();
	}

	private DialogueMessages.Messaging messaging() {
		return messages.get().getMessaging();
	}

	private void send(SocialismusPlayer player, String template, Map<String, String> placeholders) {
		player.sendMessage(Serializer.serialize(SerializerContent.builder()
				.receiver(player)
				.message(template)
				.placeholders(placeholders)
				.build()));
	}
}
