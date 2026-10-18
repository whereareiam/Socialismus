package me.whereareiam.socialismus.module.dialogue.command.executor;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.commandant.annotation.Definition;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.MessageControlService;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.jetbrains.annotations.Nullable;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class IgnoreCommand {
	private final MessageControlService controlService;

	@Definition("ignore")
	@Command("ignore [player]")
	public void onCommand(
			SocialismusPlayer player,
			@Argument(value = "player", suggestions = "players") @Nullable String target
	) {
		// Without a player, the command lists whom the player ignores.
		if (target == null) {
			controlService.showIgnored(player);
			return;
		}

		controlService.ignore(player, target);
	}
}
