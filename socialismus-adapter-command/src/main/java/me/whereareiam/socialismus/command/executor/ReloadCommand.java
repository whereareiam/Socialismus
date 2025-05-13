package me.whereareiam.socialismus.command.executor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.api.Reloadable;
import me.whereareiam.socialismus.api.Serializer;
import me.whereareiam.socialismus.api.input.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.api.model.CommandEntity;
import me.whereareiam.socialismus.api.model.config.message.CommandMessages;
import me.whereareiam.socialismus.api.model.config.message.Messages;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.command.CommandBase;
import me.whereareiam.socialismus.api.output.command.CommandCooldown;
import me.whereareiam.socialismus.api.util.EventUtil;
import net.kyori.adventure.audience.Audience;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.Map;
import java.util.Set;

@Singleton
public class ReloadCommand extends CommandBase {
	private static final String COMMAND_NAME = "reload";
	private final Provider<Map<String, CommandEntity>> commands;
	private final Set<Reloadable> reloadables;
	private final Provider<Messages> messages;

	@Inject
	public ReloadCommand(
			Provider<Map<String, CommandEntity>> commands,
			@Named("reloadables") Set<Reloadable> reloadables,
			Provider<Messages> messages
	) {
		super(COMMAND_NAME);
		this.commands = commands;

		this.reloadables = reloadables;
		this.messages = messages;
	}

	@Command("%command." + COMMAND_NAME)
	@CommandDescription("%description." + COMMAND_NAME)
	@CommandCooldown("%cooldown." + COMMAND_NAME)
	@Permission("%permission." + COMMAND_NAME)
	public void onCommand(DummyPlayer dummyPlayer) {
		Audience audience = dummyPlayer.getAudience();
		Messages messages = this.messages.get();
		CommandMessages.ReloadCommand commandMessages = messages.getCommands().getReloadCommand();

		audience.sendMessage(Serializer.serialize(dummyPlayer, commandMessages.getReloading()));

		try {
			if (EventUtil.callEvent(new PluginReloadedEvent(false), () -> reloadables.forEach(Reloadable::reload)))
				audience.sendMessage(Serializer.serialize(dummyPlayer, commandMessages.getReloaded()));
			else audience.sendMessage(Serializer.serialize(dummyPlayer, messages.getCommands().getCancelled()));
		} catch (Exception e) {
			audience.sendMessage(Serializer.serialize(dummyPlayer, commandMessages.getException().replace("{exception}", e.getMessage())));
		}
	}

	@Override
	public CommandEntity getCommandEntity() {
		return commands.get().get(COMMAND_NAME);
	}
}
