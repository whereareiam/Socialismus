package me.whereareiam.socialismus.module.dialogue.command.executor;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.commandant.annotation.Definition;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.MessageModerationService;
import org.incendo.cloud.annotations.Command;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SpyCommand {
	private final MessageModerationService moderationService;

	@Definition("spy")
	@Command("socialspy")
	public void onCommand(SocialismusPlayer player) {
		moderationService.toggleSpy(player);
	}
}
