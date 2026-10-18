package me.whereareiam.socialismus.command.executor;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.commandant.annotation.Definition;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.service.chat.ChatToggleService;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.jetbrains.annotations.NotNull;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ToggleCommand {
	private final ChatToggleService toggleService;

	@Definition("toggle")
	@Command("socialismus toggle <chat>")
	public void command(@NotNull SocialismusPlayer player, @Argument(value = "chat", suggestions = "toggleableChats") String chat) {
		toggleService.toggle(player, chat);
	}
}
