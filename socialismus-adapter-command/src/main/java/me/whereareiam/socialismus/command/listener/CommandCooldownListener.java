package me.whereareiam.socialismus.command.listener;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Actor;
import me.whereareiam.keystone.Player;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.config.message.Messages;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.processors.cooldown.CooldownInstance;
import org.incendo.cloud.processors.cooldown.listener.CooldownActiveListener;

import java.time.Duration;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class CommandCooldownListener implements CooldownActiveListener<Actor> {
	private final Provider<Messages> messages;

	@Override
	public void cooldownActive(@NonNull Actor actor, @NonNull Command<Actor> command, @NonNull CooldownInstance cooldown, @NonNull Duration remainingTime) {
		Logger.debug("Cooldown active for " + resolveActorIdentifier(actor) + " on command " + command.rootComponent().name() + " for " + remainingTime.getSeconds() + " seconds");

		// Format time as seconds with 2 decimal places
		String timeFormatted = String.format("%d.%02d", remainingTime.getSeconds(), remainingTime.getNano() / 10_000_000);

		actor.sendMessage(Serializer.serialize(SerializerContent.builder()
				.receiver(actor)
				.message(messages.get().getCommands().getCooldown())
				.placeholder("{time}", timeFormatted)
				.build()));
	}

	private String resolveActorIdentifier(@NonNull Actor actor) {
		if (actor instanceof Player) {
			return ((Player) actor).getUsername();
		}

		return actor.getClass().getSimpleName();
	}
}
