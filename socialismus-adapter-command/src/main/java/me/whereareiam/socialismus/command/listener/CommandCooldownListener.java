package me.whereareiam.socialismus.command.listener;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.Serializer;
import me.whereareiam.socialismus.api.model.config.message.Messages;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.serializer.SerializerContent;
import me.whereareiam.socialismus.api.model.serializer.SerializerPlaceholder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.processors.cooldown.CooldownInstance;
import org.incendo.cloud.processors.cooldown.listener.CooldownActiveListener;

import java.time.Duration;
import java.util.List;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class CommandCooldownListener implements CooldownActiveListener<DummyPlayer> {
	private final Provider<Messages> messages;

	@Override
	public void cooldownActive(@NonNull DummyPlayer dummyPlayer, @NonNull Command<DummyPlayer> command, @NonNull CooldownInstance cooldown, @NonNull Duration remainingTime) {
		Logger.debug("Cooldown active for " + dummyPlayer.getUsername() + " on command " + command.rootComponent().name() + " for " + remainingTime.getSeconds() + " seconds");

		dummyPlayer.sendMessage(Serializer.serialize(new SerializerContent(
				dummyPlayer,
				List.of(new SerializerPlaceholder("{time}", remainingTime.getSeconds() + "." + String.valueOf(remainingTime.getNano()).substring(0, 2))),
				messages.get().getCommands().getCooldown()
		)));
	}
}
