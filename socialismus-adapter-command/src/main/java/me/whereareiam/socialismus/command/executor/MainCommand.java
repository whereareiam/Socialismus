package me.whereareiam.socialismus.command.executor;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.commandant.annotation.Definition;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MainCommand {
	private final HelpCommand helpCommand;

	@Definition("main")
	@Command("socialismus")
	public void command(@NotNull Actor sender) {
		helpCommand.command(sender, 1);
	}
}
