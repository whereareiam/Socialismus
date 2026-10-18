package me.whereareiam.socialismus.module.dialogue.common;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueMessages;
import me.whereareiam.socialismus.module.dialogue.api.model.conversation.Conversation;
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
