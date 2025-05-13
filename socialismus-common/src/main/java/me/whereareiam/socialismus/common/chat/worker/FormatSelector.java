package me.whereareiam.socialismus.common.chat.worker;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

		workerProcessor.addWorker(new Worker<>(this::formatChat, 1, true, false));
	}

	private FormattedChatMessage formatChat(FormattedChatMessage formattedChatMessage) {
		Logger.debug("Formatting chat message for user " + formattedChatMessage.getSender().getUsername());

		ChatFormat chatFormat = formattedChatMessage.getChat().getFormats().get(formattedChatMessage.getChat().getFormats().size() - 1);
		if (chatFormat == null || !checkRequirements(chatFormat, formattedChatMessage)) {
			notifyAboutAbsentFormat(formattedChatMessage);
			formattedChatMessage.setCancelled(true);

			return formattedChatMessage;
		}

		Logger.debug("Selected format: " + chatFormat);
		formattedChatMessage.setFormat(Serializer.serialize(
				formattedChatMessage.getSender(),
				chatFormat.getFormat()
		));

		return formattedChatMessage;
	}

	private boolean checkRequirements(ChatFormat chatFormat, FormattedChatMessage formattedChatMessage) {
		if (chatFormat.getRequirements().get(Participants.SENDER) == null) return true;
		if (!requirementEvaluator.check(chatFormat.getRequirements().get(Participants.SENDER), formattedChatMessage.getSender()))
			chatFormat = getAlternativeChatFormat(chatFormat, formattedChatMessage);

		return chatFormat != null;
	}

	private ChatFormat getAlternativeChatFormat(ChatFormat chatFormat, FormattedChatMessage formattedChatMessage) {
		List<ChatFormat> formats = new ArrayList<>(formattedChatMessage.getChat().getFormats());
		Collections.reverse(formats);

		formats.remove(chatFormat);
		for (ChatFormat alternativeChat : formats)
			if (checkRequirements(alternativeChat, formattedChatMessage))
				return alternativeChat;

		return null;
	}

	private void notifyAboutAbsentFormat(FormattedChatMessage formattedChatMessage) {
		if (!chatSettings.get().isNotifyNoFormat()) return;

		formattedChatMessage.getSender().sendMessage(
				Serializer.serialize(formattedChatMessage.getSender(), chatMessages.get().getNoChatMatch())
		);
	}
}
