package me.whereareiam.socialismus.common.chat.worker.formatted;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.Serializer;
import me.whereareiam.socialismus.api.input.WorkerProcessor;
import me.whereareiam.socialismus.api.model.Worker;
import me.whereareiam.socialismus.api.model.chat.ChatFormat;
import me.whereareiam.socialismus.api.model.chat.ChatMessages;
import me.whereareiam.socialismus.api.model.chat.ChatSettings;
import me.whereareiam.socialismus.api.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.api.type.chat.Participants;
import me.whereareiam.socialismus.common.requirement.RequirementEvaluator;

@Singleton
public class FormatSelector {
	private final RequirementEvaluator requirementEvaluator;

	// Configs
	private final Provider<ChatMessages> chatMessages;
	private final Provider<ChatSettings> chatSettings;

	@Inject
	public FormatSelector(
			WorkerProcessor<FormattedChatMessage> workerProcessor,
			RequirementEvaluator requirementEvaluator,
			Provider<ChatMessages> chatMessages,
			Provider<ChatSettings> chatSettings
	) {
		this.requirementEvaluator = requirementEvaluator;
		this.chatMessages = chatMessages;
		this.chatSettings = chatSettings;

		// init configs
		chatMessages.get();

		workerProcessor.addWorker(new Worker<>(this::formatChat, 0, true, false));
	}

	private FormattedChatMessage formatChat(FormattedChatMessage msg) {
		Logger.debug("Formatting chat message for user " + msg.getSender().getUsername());

		ChatFormat chosen = selectFormat(msg);

		if (chosen == null) {
			notifyAboutAbsentFormat(msg);
			msg.setCancelled(true);
			return msg;
		}

		Logger.debug("Selected format: " + chosen);
		msg.setFormat(Serializer.serialize(msg.getSender(), chosen.getFormat()));
		return msg;
	}

	private ChatFormat selectFormat(FormattedChatMessage msg) {
		for (ChatFormat format : msg.getChat().getFormats().reversed()) {
			if (isAllowed(format, msg))
				return format;
		}
		return null;
	}

	private boolean isAllowed(ChatFormat format, FormattedChatMessage msg) {
		if (format.getRequirements() == null) return true;
		if (format.getRequirements().get(Participants.SENDER) == null) return true;

		return requirementEvaluator.check(
				format.getRequirements().get(Participants.SENDER),
				msg.getSender()
		);
	}

	private void notifyAboutAbsentFormat(FormattedChatMessage msg) {
		if (!chatSettings.get().isNotifyNoFormat()) return;

		msg.getSender().sendMessage(
				Serializer.serialize(msg.getSender(), chatMessages.get().getNoChatMatch())
		);
	}
}
