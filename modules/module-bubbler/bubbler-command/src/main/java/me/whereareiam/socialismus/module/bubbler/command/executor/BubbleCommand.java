package me.whereareiam.socialismus.module.bubbler.command.executor;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.commandant.annotation.Definition;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.bubbler.api.BubbleCoordinationService;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.type.ActivatorType;
import net.kyori.adventure.text.Component;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

import java.util.Set;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BubbleCommand {
	private final BubbleCoordinationService coordinationService;

	@Definition("bubble")
	@Command("socialismus bubble <message>")
	public void onCommand(SocialismusPlayer player, @Argument(value = "message") String message) {
		coordinationService.coordinate(BubbleMessage.builder()
				.sender(player)
				.recipients(Set.of())
				.content(Component.text(message))
				.activatorType(ActivatorType.COMMAND)
				.build()
		);
	}
}
