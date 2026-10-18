package me.whereareiam.socialismus.module.dialogue.command.executor;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.commandant.annotation.Definition;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.MessageControlService;
import org.incendo.cloud.annotations.Command;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ToggleCommand {
	private final MessageControlService controlService;

	@Definition("toggle")
	@Command("msgtoggle")
	public void onCommand(SocialismusPlayer player) {
		controlService.toggleMessages(player);
	}
}
