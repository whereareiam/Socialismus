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

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ReplyCommand {
	private final DialogueService dialogueService;

	@Definition("reply")
	@Command("reply <message>")
	public void onCommand(
			SocialismusPlayer player,
			@Argument(value = "message") @Greedy String message
	) {
		dialogueService.reply(player, message);
	}
}
