package me.whereareiam.socialismus.common.chat.worker.base;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.common.chat.toggle.ChatToggles;
import me.whereareiam.socialismus.event.chat.recipient.RecipientsSelectedEvent;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.service.chat.ChatConditionService;
import me.whereareiam.socialismus.util.EventUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Keeps the players a message reaches: those its chat's {@code reaches} lets through and who did not turn the chat
 * off. A message that reaches nobody but its sender is kept back when {@code notifications.nobody} is on, and the
 * sender is told why.
 */
@Singleton
public class RecipientSelector {
	private final ChatConditionService conditions;
	private final Provider<ChatSettings> settingsProvider;
	private final ChatToggles toggles;
	private final NobodyNotice nobody;

	@Inject
	public RecipientSelector(
			WorkerProcessor<ChatMessage> workerProcessor,
			ChatConditionService conditions,
			Provider<ChatSettings> settings,
			ChatToggles toggles,
			NobodyNotice nobody
	) {
		this.conditions = conditions;
		this.settingsProvider = settings;
		this.toggles = toggles;
		this.nobody = nobody;

		workerProcessor.addWorker(new Worker<>(this::selectRecipients, 100, true, false));
	}

	private ChatMessage selectRecipients(ChatMessage chatMessage) {
		if (chatMessage.getChat() == null) {
			chatMessage.setCancelled(true);
			return chatMessage;
		}

		Chat chat = chatMessage.getChat();
		SocialismusPlayer sender = chatMessage.getSender();
		ChatSettings settings = settingsProvider.get();
		int beforeCount = chatMessage.getRecipients().size();

		List<Verdict.Failed> refusals = new ArrayList<>();
		Set<SocialismusPlayer> recipients = reached(chatMessage, refusals, reachesPlayersWhoTurnedItOff(chat, sender, settings));

		RecipientsSelectedEvent event = new RecipientsSelectedEvent(chatMessage, recipients, chatMessage.isCancelled());
		EventUtil.callEvent(event, () -> chatMessage.setRecipients(event.getNewRecipients()));

		Collection<SocialismusPlayer> finalRecipients = event.getNewRecipients();
		Logger.debug("Recipients before: " + beforeCount + ", after: " + finalRecipients.size());

		if (finalRecipients.isEmpty()) {
			chatMessage.setCancelled(true);
			return chatMessage;
		}

		boolean reachesOthers = finalRecipients.stream().anyMatch(recipient -> recipient != sender);
		if (reachesOthers || !settings.getNotifications().isNobody()) return chatMessage;

		if (!chatMessage.isRelayed()) nobody.tell(sender, chat, refusals);
		chatMessage.setCancelled(true);

		return chatMessage;
	}

	private Set<SocialismusPlayer> reached(ChatMessage message, List<Verdict.Failed> refusals, boolean reachesToggledOff) {
		Set<SocialismusPlayer> reached = new HashSet<>();
		for (SocialismusPlayer recipient : message.getRecipients()) {
			if (!reachesToggledOff && turnedOff(recipient, message)) continue;

			Verdict verdict = conditions.reaches(message.getChat(), recipient, message);
			if (verdict instanceof Verdict.Failed failed) refusals.add(failed);
			if (verdict.passed()) reached.add(recipient);
		}

		return reached;
	}

	private boolean turnedOff(SocialismusPlayer recipient, ChatMessage message) {
		return recipient != message.getSender() && toggles.isOff(recipient.getUniqueId(), message.getChat().getId());
	}

	/**
	 * Returns whether a message reaches the players who turned its chat off: when players may not turn chats off,
	 * when this chat may not be turned off, or when the sender meets {@code toggle.bypass}.
	 */
	private boolean reachesPlayersWhoTurnedItOff(Chat chat, SocialismusPlayer sender, ChatSettings settings) {
		ChatSettings.Toggle toggle = settings.getToggle();
		if (toggle == null || !toggle.isEnabled() || !chat.isToggleable()) return true;

		return conditions.bypassesToggle(sender);
	}
}
