package me.whereareiam.socialismus.command.executor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.Serializer;
import me.whereareiam.socialismus.api.input.chat.ChatHistoryService;
import me.whereareiam.socialismus.api.input.container.ChatHistoryContainerService;
import me.whereareiam.socialismus.api.model.CommandEntity;
import me.whereareiam.socialismus.api.model.chat.ChatSettings;
import me.whereareiam.socialismus.api.model.config.message.Messages;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.PlatformInteractor;
import me.whereareiam.socialismus.api.output.command.CommandBase;
import me.whereareiam.socialismus.api.output.command.CommandCooldown;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.Map;

@Singleton
public class ClearCommand extends CommandBase {
	private static final String COMMAND_NAME = "clear";
	private final Provider<Map<String, CommandEntity>> commands;

	private final Provider<Messages> messages;
	private final Provider<ChatSettings> chatSettings;

	private final ChatHistoryService chatHistory;
	private final ChatHistoryContainerService containerService;
	private final PlatformInteractor interactor;

	@Inject
	public ClearCommand(
			Provider<Map<String, CommandEntity>> commands,
			Provider<Messages> messages,
			Provider<ChatSettings> chatSettings,
			ChatHistoryService chatHistory,
			ChatHistoryContainerService containerService,
			PlatformInteractor interactor
	) {
		super(COMMAND_NAME);
		this.commands = commands;
		this.messages = messages;
		this.chatSettings = chatSettings;
		this.chatHistory = chatHistory;
		this.containerService = containerService;
		this.interactor = interactor;
	}

	@Command("%command." + COMMAND_NAME)
	@CommandDescription("%description." + COMMAND_NAME)
	@CommandCooldown("%cooldown." + COMMAND_NAME)
	@Permission("%permission." + COMMAND_NAME)
	public void onCommand(DummyPlayer dummyPlayer, @Argument(value = "context") String context) {
		if (context == null) {
			handleNumericContext(dummyPlayer, chatSettings.get().getHistory().getHistorySize());
			return;
		}

		try {
			handleCommand(dummyPlayer, context);
		} catch (NumberFormatException e) {
			handleNonNumericContext(dummyPlayer, context);
		}
	}

	private void handleCommand(DummyPlayer dummyPlayer, String context) {
		try {
			int number = Integer.parseInt(context);
			if (number >= 1 && number <= chatSettings.get().getHistory().getHistorySize()) {
				handleNumericContext(dummyPlayer, number);
				return;
			}

			handleInvalidNumber(dummyPlayer, number);
		} catch (NumberFormatException e) {
			handleNonNumericContext(dummyPlayer, context);
		}
	}

	private void handleNumericContext(DummyPlayer dummyPlayer, int number) {
		if (hasMinimumMessages()) {
			dummyPlayer.sendMessage(Serializer.serialize(dummyPlayer, messages.get().getCommands().getClearCommand().getNotEnoughHistory()));
			return;
		}

		int count = chatHistory.removeMessages(number);
		sendResponse(dummyPlayer, count, messages.get().getCommands().getClearCommand().getClearedAmount(), messages.get().getCommands().getClearCommand().getNoHistory());
	}

	private void handleInvalidNumber(DummyPlayer dummyPlayer, int number) {
		if (hasMinimumMessages()) {
			dummyPlayer.sendMessage(Serializer.serialize(dummyPlayer, messages.get().getCommands().getClearCommand().getNotEnoughHistory()));
			return;
		}

		boolean removed = chatHistory.removeMessage(number);
		sendResponse(dummyPlayer, removed, messages.get().getCommands().getClearCommand().getCleared(), messages.get().getCommands().getClearCommand().getNoIdHistory().replace("{id}", String.valueOf(number)));
	}

	private void handleNonNumericContext(DummyPlayer dummyPlayer, String context) {
		if (hasMinimumMessages()) {
			dummyPlayer.sendMessage(Serializer.serialize(dummyPlayer, messages.get().getCommands().getClearCommand().getNotEnoughHistory()));
			return;
		}

		if (interactor.hasPermission(context, chatSettings.get().getHistory().getBypassPermission())) {
			dummyPlayer.sendMessage(Serializer.serialize(dummyPlayer, messages.get().getCommands().getClearCommand().getBypassUser()));
			return;
		}

		int count = chatHistory.removeMessages(context);
		sendResponse(
				dummyPlayer, count,
				messages.get().getCommands().getClearCommand().getClearedAmount(),
				messages.get().getCommands().getClearCommand().getNoUserHistory()
		);
	}

	private boolean hasMinimumMessages() {
		return containerService.getMessages().size() < 5;
	}

	private void sendResponse(DummyPlayer dummyPlayer, int count, String successMessage, String failureMessage) {
		if (count > 0) {
			Logger.info("Deleted %s messages from chat history by %s", count, dummyPlayer.getUsername());
			dummyPlayer.sendMessage(Serializer.serialize(dummyPlayer, successMessage.replace("{amount}", String.valueOf(count))));
		} else {
			dummyPlayer.sendMessage(Serializer.serialize(dummyPlayer, failureMessage));
		}
	}

	private void sendResponse(DummyPlayer dummyPlayer, boolean removed, String successMessage, String failureMessage) {
		if (removed) {
			Logger.info("Deleted message from chat history by %s", dummyPlayer.getUsername());
			dummyPlayer.sendMessage(Serializer.serialize(dummyPlayer, successMessage));
		} else {
			dummyPlayer.sendMessage(Serializer.serialize(dummyPlayer, failureMessage));
		}
	}

	@Override
	public CommandEntity getCommandEntity() {
		return commands.get().get(COMMAND_NAME);
	}
}