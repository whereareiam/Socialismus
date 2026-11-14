package me.whereareiam.socialismus.common.chat.worker.formatted;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.model.chat.ChatFormat;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.service.chat.ChatConditions;

import java.util.Optional;

@Singleton
public class FormatSelector {
	private final ChatConditions chatConditions;

	// Configs
	private final Provider<ChatMessages> chatMessages;
	private final Provider<ChatSettings> chatSettings;

	@Inject
	public FormatSelector(
			WorkerProcessor<FormattedChatMessage> workerProcessor,
			ChatConditions chatConditions,
			Provider<ChatMessages> chatMessages,
			Provider<ChatSettings> chatSettings
	) {
		this.chatConditions = chatConditions;
		this.chatMessages = chatMessages;
		this.chatSettings = chatSettings;

		// init configs
		chatMessages.get();

		workerProcessor.addWorker(new Worker<>(this::formatChat, 0, true, false));
	}

	private FormattedChatMessage formatChat(FormattedChatMessage msg) {
		Logger.debug("Formatting chat message for user " + msg.getSender().getUsername());

		Optional<ChatFormat> chosen = chatConditions.of(msg.getChat()).flatMap(places -> places.format(msg));
		if (chosen.isEmpty()) {
			notifyAboutAbsentFormat(msg);
			msg.setCancelled(true);
			return msg;
		}

		Logger.debug("Selected format for user %s", msg.getSender().getUsername());
		msg.setFormat(Serializer.serialize(msg.getSender(), chosen.get().getFormat()));
		return msg;
	}

	private void notifyAboutAbsentFormat(FormattedChatMessage msg) {
		if (!chatSettings.get().getNotifications().isNoFormat()) return;

		msg.getSender().sendMessage(
				Serializer.serialize(msg.getSender(), chatMessages.get().getNoFormatMatch())
		);
	}
}
