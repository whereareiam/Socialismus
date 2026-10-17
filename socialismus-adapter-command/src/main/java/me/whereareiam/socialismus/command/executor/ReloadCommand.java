package me.whereareiam.socialismus.command.executor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.commandant.annotation.Definition;
import me.whereareiam.keystone.Actor;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.config.message.CommandMessages;
import me.whereareiam.socialismus.model.config.message.Messages;
import org.incendo.cloud.annotations.Command;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Reloads every configuration document, in the order they registered. A document that fails to reload keeps what it
 * held and the others still reload; the answer is a success only when none failed. Afterwards it calls
 * {@link PluginReloadedEvent}, whatever failed, so what is built from the documents rebuilds.
 */
@Singleton
public class ReloadCommand {
	private final Provider<Messages> messagesProvider;
	private final Provider<Set<Reloadable>> reloadablesProvider;
	private final EventManager eventManager;

	@Inject
	public ReloadCommand(
			@NotNull Provider<Messages> messagesProvider,
			@NotNull @Named("reloadables") Provider<Set<Reloadable>> reloadablesProvider,
			@NotNull EventManager eventManager
	) {
		this.messagesProvider = messagesProvider;
		this.reloadablesProvider = reloadablesProvider;
		this.eventManager = eventManager;
	}

	@Definition("reload")
	@Command("socialismus reload")
	public void command(@NotNull Actor sender) {
		List<String> failures = new ArrayList<>();
		for (Reloadable reloadable : reloadablesProvider.get()) {
			try {
				reloadable.reload();
			} catch (Exception failure) {
				Logger.severe("Failed to reload " + reloadable.getClass().getSimpleName(), failure);
				failures.add(failure.getMessage() != null ? failure.getMessage() : failure.getClass().getSimpleName());
			}
		}

		eventManager.call(new PluginReloadedEvent());

		CommandMessages.ReloadCommand reload = messagesProvider.get().getCommands().getReloadCommand();
		if (failures.isEmpty()) {
			sender.sendMessage(Serializer.serialize(sender, reload.getReloaded()));
			return;
		}

		sender.sendMessage(Serializer.serialize(SerializerContent.builder()
				.receiver(sender)
				.message(reload.getException())
				.placeholder("exception", String.join("; ", failures))
				.build()));
	}
}
