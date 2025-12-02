package me.whereareiam.socialismus.command.executor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.commandant.annotation.Definition;
import me.whereareiam.keystone.Actor;
import me.whereareiam.keystone.Player;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.config.message.Messages;
import me.whereareiam.socialismus.service.chat.ChatHistoryService;
import me.whereareiam.socialismus.service.container.ChatHistoryContainerService;
import net.kyori.adventure.text.Component;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.jetbrains.annotations.NotNull;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ClearCommand {
	private final Provider<Messages> messages;
	private final Provider<ChatSettings> chatSettings;

	private final ChatHistoryService chatHistory;
	private final ChatHistoryContainerService containerService;
	private final PlatformInteractor interactor;

	@Definition("clear")
	@Command("socialismus clear [context]")
	public void command(@NotNull Actor actor, @Argument("context") String context) {
		if (context == null) {
			handleNumericContext(actor, chatSettings.get().getHistory().getHistorySize());
			return;
		}

		try {
			handleCommand(actor, context);
		} catch (NumberFormatException e) {
			handleNonNumericContext(actor, context);
		}
	}

	private void handleCommand(@NotNull Actor actor, String context) {
		try {
			int number = Integer.parseInt(context);
			if (number >= 1 && number <= chatSettings.get().getHistory().getHistorySize()) {
				handleNumericContext(actor, number);
				return;
			}

			handleInvalidNumber(actor, number);
		} catch (NumberFormatException e) {
			handleNonNumericContext(actor, context);
		}
	}

	private void handleNumericContext(@NotNull Actor actor, int number) {
		if (hasMinimumMessages()) {
			actor.sendMessage(Serializer.serialize(actor, messages.get().getCommands().getClearCommand().getNotEnoughHistory()));
			return;
		}

		int count = chatHistory.removeMessages(number);
		sendResponse(actor, count, messages.get().getCommands().getClearCommand().getClearedAmount(), messages.get().getCommands().getClearCommand().getNoHistory());
	}

	private void handleInvalidNumber(@NotNull Actor actor, int number) {
		if (hasMinimumMessages()) {
			actor.sendMessage(Serializer.serialize(actor, messages.get().getCommands().getClearCommand().getNotEnoughHistory()));
			return;
		}

		boolean removed = chatHistory.removeMessage(number);
		sendResponse(actor, removed, messages.get().getCommands().getClearCommand().getCleared(), messages.get().getCommands().getClearCommand().getNoIdHistory(), number);
	}

	private void handleNonNumericContext(@NotNull Actor actor, String context) {
		if (hasMinimumMessages()) {
			actor.sendMessage(Serializer.serialize(actor, messages.get().getCommands().getClearCommand().getNotEnoughHistory()));
			return;
		}

		if (interactor.hasPermission(context, chatSettings.get().getHistory().getBypassPermission())) {
			actor.sendMessage(Serializer.serialize(actor, messages.get().getCommands().getClearCommand().getBypassUser()));
			return;
		}

		int count = chatHistory.removeMessages(context);
		sendResponse(
				actor, count,
				messages.get().getCommands().getClearCommand().getClearedAmount(),
				messages.get().getCommands().getClearCommand().getNoUserHistory()
		);
	}

	private boolean hasMinimumMessages() {
		return containerService.getMessages().size() < 5;
	}

	private void sendResponse(@NotNull Actor actor, int count, String successMessage, String failureMessage) {
		if (count > 0) {
			Logger.info("Deleted %s messages from chat history by %s", count, resolveActorIdentifier(actor));
			actor.sendMessage(Serializer.serialize(SerializerContent.builder()
					.receiver(actor)
					.message(successMessage)
					.placeholder("{amount}", String.valueOf(count))
					.build())
			);
		} else {
			Component component = Serializer.serialize(actor, failureMessage);
			actor.sendMessage(component);
		}
	}

	private void sendResponse(@NotNull Actor actor, boolean removed, String successMessage, String failureMessage, int id) {
		if (removed) {
			Logger.info("Deleted message from chat history by %s", resolveActorIdentifier(actor));
			actor.sendMessage(Serializer.serialize(actor, successMessage));
			return;
		}

		actor.sendMessage(Serializer.serialize(SerializerContent.builder()
				.receiver(actor)
				.message(failureMessage)
				.placeholder("{id}", String.valueOf(id))
				.build())
		);
	}

	private String resolveActorIdentifier(@NotNull Actor actor) {
		if (actor instanceof Player) {
			return ((Player) actor).getUsername();
		}

		return actor.getClass().getSimpleName();
	}
}