package me.whereareiam.socialismus.module.dialogue.command.executor;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.commandant.annotation.Definition;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.DialogueService;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.jetbrains.annotations.Nullable;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MessageCommand {
	private final DialogueService dialogueService;

	@Definition("message")
	@Command("message <recipient> [message]")
	public void onCommand(
			SocialismusPlayer player,
			@Argument(value = "recipient", suggestions = "crossPlayers") String recipientName,
			@Argument(value = "message") @Greedy @Nullable String message
	) {
		// Without a message, the command starts or ends a conversation with the recipient.
		if (message == null) {
			dialogueService.converse(player, recipientName);
			return;
		}

		dialogueService.send(player, recipientName, message);
	}
}
