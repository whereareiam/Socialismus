package me.whereareiam.socialismus.command.executor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.commandant.annotation.Definition;
import me.whereareiam.socialismus.api.Reloadable;
import me.whereareiam.socialismus.api.Serializer;
import me.whereareiam.socialismus.api.input.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.api.model.config.message.CommandMessages;
import me.whereareiam.socialismus.api.model.config.message.Messages;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.util.EventUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.incendo.cloud.annotations.Command;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Singleton
public class ReloadCommand {
	private final Provider<Messages> messagesProvider;
	private final Provider<Set<Reloadable>> reloadablesProvider;

	@Inject
	public ReloadCommand(
			@NotNull Provider<Messages> messagesProvider,
			@NotNull @Named("reloadables") Provider<Set<Reloadable>> reloadablesProvider
	) {
		this.messagesProvider = messagesProvider;
		this.reloadablesProvider = reloadablesProvider;
	}

	@Definition("reload")
	@Command("intercept reload")
	public void command(@NotNull Actor sender) {
		Messages.Commands.Reload reload = messagesProvider.get().getCommands().getReload();

		try {
			// Reload all registered reloadable components
			Set<Reloadable> reloadables = reloadablesProvider.get();
			for (Reloadable reloadable : reloadables)
				reloadable.reload();

			reload = messagesProvider.get().getCommands().getReload();

			Component component = Serializer.serialize(sender, reload.getSuccess());
			sender.sendMessage(component);
		} catch (Exception e) {
			Component component = Serializer.serialize(SerializerContent.builder()
					.receiver(sender)
					.message(reload.getError())
					.placeholder("error", e.getMessage())
					.build());
			sender.sendMessage(component);
		}
	}
}
