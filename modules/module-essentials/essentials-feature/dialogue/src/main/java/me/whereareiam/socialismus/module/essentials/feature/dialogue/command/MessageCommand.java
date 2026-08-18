package me.whereareiam.socialismus.module.essentials.feature.dialogue.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.commandant.annotation.Definition;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.dialogue.DialogueManager;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MessageCommand {
	private final DialogueManager dialogueManager;

	@Definition("message")
	@Command("message <recipient> <message>")
	public void onCommand(
			SocialismusPlayer player,
			@Argument(value = "recipient", suggestions = "crossPlayers") String recipientName,
			@Argument(value = "message") @Greedy String message
	) {
		dialogueManager.send(player, recipientName, message);
	}
}
